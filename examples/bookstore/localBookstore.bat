@echo off

if exist "com\abc\bookstore\application\LocalBookstore.class" goto bookstoreBuilt
@echo "Bookstore not built. Calling ANT"
call ant
:bookstoreBuilt

java -cp "..\..\lib\argot-1.3.b.jar;." com.abc.bookstore.application.LocalBookstore
