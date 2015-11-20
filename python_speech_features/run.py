from features import mfcc
from features import logfbank
import numpy
import scipy.io.wavfile as wav
import sys

print sys.argv
for i in range(1, len(sys.argv)):
    (rate,sig) = wav.read(sys.argv[i])
    mfcc_feat = mfcc(sig,rate)
    outfile = open(sys.argv[i]+'.txt', 'w')
    numpy.set_printoptions(threshold='nan')
    outfile.write(str(mfcc_feat))
