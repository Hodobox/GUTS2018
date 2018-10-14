
# REFERENCES
# https://www.researchgate.net/publication/305983877_Geoplotlib_a_Python_Toolbox_for_Visualizing_Geographical_Data
# git repo: https://github.com/andrea-cuttone/geoplotlib
# wiki: https://github.com/andrea-cuttone/geoplotlib/wiki/User-Guide#adding-interactivity-to-layers

import geoplotlib

thedata = geoplotlib.utils.read_csv('../../../data/plot.csv')


# basic dot map
geoplotlib.dot(thedata)


# heat map

#geoplotlib.kde(thedata, bw=[5,5])


# lowering clip_above changes the max value in the color scale
#geoplotlib.kde(thedata, bw=5, cut_below=1e-4, clip_above=.1)

# different bandwidths
#geoplotlib.kde(thedata, bw=20, cmap='PuBuGn', cut_below=1e-4)
#geoplotlib.kde(thedata, bw=2, cmap='PuBuGn', cut_below=1e-4)



geoplotlib.show()