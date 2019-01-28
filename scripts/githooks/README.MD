# Githooks specific to MOSIP project.

For activating the Githooks, the user has to pull the new code and run the following command (only once):

`git config --global core.hooksPath scripts/githooks/`   (From cloned Mosip repository)

After executing the above command, the configuration will be added globally for mosip repo. (User can find the configuration in your system under your User directory by filename .gitconfig)

This will ensure that any user will not be able to commit any code unless the commit message starts with MOS-xxxx or merge for merge commits.
