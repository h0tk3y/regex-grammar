pushd
cd build/install/RegexGrammar/
call run.bat %1 out.dot
move out.png ../../../out.png
cd ../../..
popd