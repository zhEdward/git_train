@echo off
echo this is inout description
set /p ip=输入本地IP:
set hosts="127.0.0.1"
echo %ip%
if %ip%==y (echo yes) else (echo not bad)

pause