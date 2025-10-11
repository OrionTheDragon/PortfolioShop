@echo off
cd /d "C:\Users\Ryzen\IdeaProjects\Shop"

:loop
git add .
git commit -m "Auto-commit at %date% %time%" >nul 2>&1
git push origin main >nul 2>&1
timeout /t 30 >nul
goto loop