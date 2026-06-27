Here is a clean, bite-sized reference note that you can save for future use.
------------------------------
## 📝 Quick Notes: Installing PostgreSQL via Chocolatey on Windows## 1. Prerequisites

* Windows OS.
* Chocolatey must be installed on your system. [1] 

## 2. Installation Steps

   1. Open PowerShell or Command Prompt as an Administrator.
   2. Run the installation command (choose the latest version or a specific version below). [2] 

## To install the latest stable version:

choco install postgresql --params "'/Password:YourStrongPasswordHere'"

## To install a specific version (e.g., v16):

choco install postgresql16 --params "'/Password:YourStrongPasswordHere'"

⚠️ Important: Replace YourStrongPasswordHere with your actual desired master password.

## 3. Key Installation Parameters
You can customize your installation by appending these inside the single quotes:

* /Port:5432 — Specifies the port (default is 5432).
* /DataDir:'C:\pgdata' — Changes the location where your database files are physically stored. [3] 

## 4. Verification & First Login
Restart your terminal to refresh the environment path variables, then log in using the default master superuser account:

psql -U postgres

## 5. Essential psql Shortcut Commands
Once logged inside the postgres=# prompt:

* \du — List all database users/roles.
* \l — List all databases.
* \q — Quit/exit the PostgreSQL prompt. [4, 5] 

------------------------------
Would you like me to add a quick section on how to completely uninstall PostgreSQL using Chocolatey if you ever need a fresh start?

[1] [https://hackolade.com](https://hackolade.com/help/Windows.html)
[2] [https://medium.com](https://medium.com/@msaqibshah001/installing-the-make-command-on-windows-with-chocolatey-39870625c03b)
[3] [https://www.postgresql.org](https://www.postgresql.org/docs/15/install-procedure.html)
[4] [https://medium.com](https://medium.com/r3d-buck3t/command-execution-with-postgresql-copy-command-a79aef9c2767)
[5] [https://www.sqlshack.com](https://www.sqlshack.com/how-to-install-postgresql-on-ubuntu/)
