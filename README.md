# WordCloud

## Compiling Java files using Eclipse IDE

1. Download this repository as ZIP
2. Create new `Java Project` in `Eclipse`
3. Right click on your `Java Project` --> `Import`
4. Choose `General` --> `Archive File`
5. Put directory where you downloaded ZIP in `From archive file`
6. Put `ProjectName/src` in `Into folder`
7. Click `Finish`
8. Move `text files/some-common-words.txt` from {ProjectName}/src to the root of your Java Project i.e. {ProjectName}

### Linking the UI Library

8. Right click on your `Java Project` --> `Build Path` --> `Add External Archives`
9. Select `ecs100.jar` and link it to the project. That JAR will be in the directory where you downloaded ZIP

## Running the program

1. Right click on your `Java Project` --> `Run As` --> `Java Application` --> `WordCloud`
2. Choose `<file1>.txt` and `<file2>.txt`

## Features

### Remove Standard and Common Words

Keeping words NOT present in `some-common-words.txt`

### Remove Infrequent Words

Keeps only the most common `N` words where `N` = 100 initially and goes all the way down to 50

### Remove Unshared Words

Keeping only words that occur in BOTH files 
