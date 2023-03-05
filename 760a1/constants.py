# Global constants
NO_CRUCIBLES = 17
NO_POTS = 51
POTS_PER_CRUCIBLE = 3
NO_QUALITIES = 11
QUALITY_MIN_AL = [
    95.00, 99.10, 99.10, 99.20, 99.25, 99.35, 99.50, 99.65, 99.75, 99.85, 99.90,
]
QUALITY_MAX_FE = [
    5.00, 0.81, 0.79, 0.79, 0.76, 0.72, 0.53, 0.50, 0.46, 0.33, 0.30,
]
QUALITY_VALUE = [
    10.00, 21.25, 26.95, 36.25, 41.53, 44.53, 48.71, 52.44, 57.35, 68.21, 72.56,
]
POT_AL = [
    99.79, 99.23, 99.64, 99.88, 99.55, 99.87, 99.55, 99.19, 99.76, 99.70, 99.26, 99.60, 99.05,
    99.49, 99.69, 99.48, 99.60, 99.89, 99.39, 99.48, 99.77, 99.57, 99.48, 99.85, 99.09, 99.64,
    99.71, 99.59, 99.14, 99.87, 99.38, 99.56, 99.32, 99.55, 99.61, 99.57, 99.75, 99.63, 99.17,
    99.97, 99.74, 99.49, 99.75, 99.40, 99.72, 99.95, 99.31, 99.55, 99.29, 99.09, 99.20,
]
POT_FE = [
    0.01, 0.68, 0.25, 0.61, 0.13, 0.77, 0.48, 0.18, 0.66, 0.43, 0.13, 0.87, 0.96, 0.47, 0.51,
    0.73, 0.04, 0.76, 0.89, 0.90, 0.96, 0.73, 0.88, 0.43, 0.60, 0.37, 0.51, 0.26, 0.30, 0.46,
    0.21, 0.77, 0.43, 0.52, 0.63, 0.76, 0.02, 0.75, 0.90, 0.53, 0.14, 0.10, 0.31, 0.20, 0.45,
    0.67, 0.56, 0.24, 0.72, 0.56, 0.01,
]

H = [(c,p,a,b) for c in range(0,16) for p in range(c+1,17) for a in [0,1,2] for b in [0,1,2]]