./gradlew run > /dev/null 2> /dev/null &
sleep 20
./scripts/create_first_account.sh
./gradlew gatlingRun
