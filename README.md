# weka
weka mirror with git  —  http://www.cs.waikato.ac.nz/ml/weka/

**This is a fork in which I've developed the ability to save a RandomTree model into a JSON format.**

Notes:

* I started from the commit tagged as `weka-dev-3.7.10`.

* The tree is saved in a JSON format that is based on BigML's format, which is described in all generality [here](https://github.com/bigmlcom/json-pml).

* I have not implemented the full BigML format, only the bare minimum needed to get [this Sunburst d3.js visualization](http://bl.ocks.org/ashenfad/5234819)
  to work with Weka RandomTree models. The JSON file that is used in this visualization can be found
  [here](https://gist.githubusercontent.com/ashenfad/5234819/raw/a7486637c67b4cfbbb05abf7155fb0febe654c13/cover-model.json).
