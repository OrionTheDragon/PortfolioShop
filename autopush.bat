@echo off
setlocal

cd /d "C:\Users\Ryzen\IdeaProjects\Shop" || (
  echo Не удалось перейти в каталог >> autopush.log
  exit /b 1
)

echo ------------------ %date% %time% ------------------ >> autopush.log
echo Running where git: >> autopush.log
where git >> autopush.log 2>&1

echo Git version: >> autopush.log
git --version >> autopush.log 2>&1

:loop
echo Проверка изменений %date% %time% >> autopush.log
git status --porcelain >> autopush.log 2>&1

rem если есть изменения — сделаем коммит и пуш
for /f "delims=" %%i in ('git status --porcelain') do set HAS_CHANGES=1

if defined HAS_CHANGES (
  echo Есть изменения. Добавляю и коммичу... >> autopush.log
  git add . >> autopush.log 2>&1
  git commit -m "Auto-commit at %date% %time%" >> autopush.log 2>&1
  echo Пушу... >> autopush.log
  git push origin main >> autopush.log 2>&1
  set HAS_CHANGES=
) else (
  echo Изменений нет. >> autopush.log
)

timeout /t 30 >nul
goto loop