set -e

./gradlew clean -p crowdin -x lint build -PversionName=$1
./gradlew -p crowdin publishGithub -PversionName=$1