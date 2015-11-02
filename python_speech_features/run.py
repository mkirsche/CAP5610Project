from features import mfcc
from features import logfbank
import scipy.io.wavfile as wav
import sys

print sys.argv
for i in range(1, len(sys.argv)):
    (rate,sig) = wav.read(sys.argv[i])
    mfcc_feat = mfcc(sig,rate)
    fbank_feat = logfbank(sig,rate)
    outfile = open(sys.argv[i]+'.txt', 'w')
    outfile.write(str(fbank_feat[1:3,:]))
