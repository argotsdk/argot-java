@echo off

if exist "com\abc\bookstore\application\LocalBookstore.class" goto bookstoreBuilt
@echo "Bookstore not built. Calling ANT"
call ant
:bookstoreBuilt

java -cp "..\..\lib\Argot-EA1.0.1.jar;." com.abc.bookstore.application.LocalBookstore
