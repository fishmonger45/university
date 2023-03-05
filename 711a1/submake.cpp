#include "submake.hpp"
#include "acout.cpp"

using namespace std;

// build a tree concurrently, executing each target concurrently where possible
void *submake::build(void *tree_vp)
{
    tree *t = (tree *)tree_vp;
    vector<pthread_t> ctid{};

    // do not rebuild if target does not need to be recompiled
    if (!submake::requires_build(t))
    {
        acout() << "skipping building for "
                << t->node->output
                << "... (file \""
                << t->node->output
                << "\" is up to date)"
                << endl;
        return NULL;
    }

    // build each child make target
    for (tree *c : t->children)
    {
        pthread_t thread_id;
        if (pthread_create(&thread_id, NULL, build, (void *)c))
        {
            cerr << "unable to create thread" << endl;
        }
        acout() << "spawned thread: " << thread_id << ", for analysing make target: " << c->node->output << endl;
        ctid.push_back(thread_id);
    }

    // join all child make target threads
    for (auto thread_id : ctid)
    {
        if (pthread_join(thread_id, NULL))
        {
            cerr << "unable to join thread" << endl;
        }
    }

    // after children have been built, build this target on the current thread
    for (string command : t->node->commands)
    {
        acout() << "executing command: \"" << command << "\"" << endl;
        system(command.c_str());
    }

    return NULL;
}

// determine if the target requires rebuild
// if the target name does not link to a file then automatically require rebuild
// otherwise, compare input file modificaiton times
bool submake::requires_build(tree *t)
{
    queue<tree *> q;
    q.push(t);

    struct stat r1;
    time_t output_modification_time;
    if (stat(t->node->output.c_str(), &r1) == 0)
    {
        output_modification_time = r1.st_mtime;
    }
    else
    {
        return true;
    }
    while (!q.empty())
    {
        tree *t = q.front();
        q.pop();
        for (tree *c : t->children)
        {
            struct stat r2;
            time_t input_modification_time;
            for (string i : c->node->input)
            {
                if (stat(i.c_str(), &r2) == 0)
                {
                    input_modification_time = r2.st_mtime;
                    if (input_modification_time >= output_modification_time)
                    {
                        acout() << "file not up to date " << i << ", building..." << endl;
                        return true;
                    }
                }
                else
                {
                    acout() << "non file target: " << i << ", building..." << endl;
                    return true;
                }
            }
            q.push(c);
        }
    }
    return false;
}

// strip a file of its extension
// eg: main.cpp -> main
string submake::strip(string s)
{
    return s.substr(0, s.find_last_of("."));
}

// parse a makefile into a tree organised by target dependencies
tree *submake::parse(string filename, string make_target)
{
    target *t = new target(); // current target
    fstream f;
    f.open(filename);           // open file
    skipws(f);                  // skip all prelude whitespaces
    vector<target *> targets{}; // parsed targets

    // flag to start the parsing of a new make target
    bool create_new_target = false;
    while (!f.eof()) // parse all targets
    {
        // string buffer from file
        string buffer;
        getline(f, buffer);

        // is the current line a target header definition
        bool is_target_header = buffer.find("\t") == string::npos;

        //start a new target after all commands parsed
        if (is_target_header && create_new_target == true)
        {
            create_new_target = false;
            targets.push_back(t);
            t = new target(); // create a new target
        }

        if (is_target_header && create_new_target == false) // is target header
        {
            create_new_target = true;
            istringstream iss(buffer);

            vector<string> tokens;
            // split header into whitespace seperated tokens
            copy(istream_iterator<string>(iss),
                 istream_iterator<string>(),
                 back_inserter(tokens));

            string output = tokens[0];
            t->output = output;
            string base = strip(output);

            // skip index of target output name and ':' character
            for (int i = 2; i < tokens.size(); i++)
            {
                t->input.push_back(tokens[i]);
            }
        }
        else
        {
            t->commands.push_back(buffer);
        }
    }

    vector<tree *> ys{};
    // create tree for each target
    for (target *another : targets)
    {
        ys.push_back(new tree(another, vector<tree *>{}));
    }

    // build dependency tree links between targets
    for (int i = 0; i < ys.size(); ++i)
    {
        tree *cur = ys[i];
        for (int j = 0; j < ys.size(); ++j)
        {
            auto it = find(cur->node->input.begin(), cur->node->input.end(), ys[j]->node->output);
            if (it != cur->node->input.end())
            {
                cur->children.push_back(ys[j]);
            }
        }
    }

    // select subtree containing target and all it's dependencies that is required
    tree *selected_subtree = NULL;
    for (int i = 0; i < ys.size(); ++i)
    {
        if (ys[i]->node->output == make_target)
        {
            selected_subtree = ys[i];
        }
    }
    return selected_subtree;
}
