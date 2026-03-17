# Next tasks for the vibe agent

Implement the following tasks one after the other. 
Don't forget to add tests for each task.
Once you are satisfied and the tests pass
* remove the entry here
* commit add the relevant files with `git add` 
* commit with `git commit -a -m "a helpful commit message"`.
If you get stuck, then abort and ask for help.


## Update pages
* there should be an --update command, that accepts a page name (for the server) and a local filename (for the content to upload).
* optionally, a comment can also be passed
* this should work for updating pages, but also for creating new pages

## Pagination for --read-category
* --read-category is currently limited to 500 categories to be read
* we need to make this configurable. 
* by default, read as many categories as there are (if there is no option for this, set the limit to a crazy high number, like 1000000.
* if possible, add pagination support where the first and last entry (by index) is specified, e.g. `--read-category Straße 1 500`

