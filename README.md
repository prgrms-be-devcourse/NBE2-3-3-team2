# NBE2-3-2-team2
프로그래머스 백엔드 데브코스 2기 Team3 2차 프로젝트 레파지토리입니다.

# Local execution

## Prerequisites
1. Maria DB 로컬 세팅

- Option1 - Docker
도커 설치 후, Terminal에서 아래 커맨드 실행 (상세 정보는 .env를 참고하여 변경)
```bash
docker run --detach --name some-mariadb --env MARIADB_DATABASE=letmovie --env MARIADB_ROOT_PASSWORD='!123456' -p 3306:3306 mariadb:latest
```