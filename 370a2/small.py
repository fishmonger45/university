from fuse import FUSE, FuseOSError, Operations, LoggingMixIn
import disktools as dt
import logging

from collections import defaultdict
from errno import ENOENT
from stat import S_IFDIR, S_IFLNK, S_IFREG
from time import time



class Bitmask:
    """ bitmask is a namespace of static methods relating to bit manipulation"""
    @staticmethod
    def test_bit(int_type, offset):
        """ test_bit returns a nonzero result, 2**offset, if the bit at 'offset' is one """
        mask = 1 << offset
        return (int_type & mask)

    @staticmethod
    def set_bit(int_type, offset):
        """ set_bit returns an integer with the bit at 'offset' set to 1 """
        mask = 1 << offset
        return (int_type | mask)

    @staticmethod
    def clear_bit(int_type, offset):
        """ clear_bit returns an integer with the bit at 'offset' cleared """
        mask = ~(1 << offset)
        return (int_type & mask)

    @staticmethod
    def toggle_bit(int_type, offset):
        """ toggle_bit returns an integer with the bit at 'offset' inverted, 0 -> 1 and 1 -> 0 """
        mask = 1 << offset
        return (int_type ^ mask)


class File():
    # a file (or directory) in the file system
    def __init__(self, flag, attributes, contents):
        self.flag = flag
        if (self.flag == 1):
            # directory
        else:
            # file



class Memory(LoggingMixIn, Operations):
    def __init__(self):

        empty = True 
        for i in range(0, 16):
            if dt.bytes_to_int(dt.read_block(i)) != 0:
                flag = False
                break

        if empty:
            # empty partition, init & write
            self.bitmask = 0
            print("empty")
        else:
            # existing partition, read
            print("non empty")

        # read bitmap
        self.bitmask = dt.bytes_to_int(dt.read_block(0)[0:28])
        print(self.bitmask)


if __name__ == '__main__':

    import argparse
    print("file system")
    # write bitmap

    parser = argparse.ArgumentParser()
    parser.add_argument('mount')
    args = parser.parse_args()

    logging.basicConfig(level=logging.DEBUG)
    fuse = FUSE(Memory(), args.mount, foreground=True)
    exit(0)
