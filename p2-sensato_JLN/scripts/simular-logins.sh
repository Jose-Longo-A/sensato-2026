#!/usr/bin/env bash
# Gera logins válidos, inválidos, bloqueios e um desbloqueio para alimentar o dashboard do Grafana.
# Uso (Git Bash / Linux / macOS):  ./scripts/simular-logins.sh [http://localhost:8080]
# Leva ~1min20s, com pausas para os gráficos mostrarem a evolução no tempo.

BASE_URL=${1:-http://localhost:8080}
MANUTENCAO="manutencao:manut123"
ADMIN="admin:admin123"
PAUSA=20

login() {
    local status
    status=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/login" \
        -H 'Content-Type: application/json' -d "{\"username\":\"$1\",\"senha\":\"$2\"}")
    echo "POST /login        $1 / $2 -> $status"
}

cadastrar() {
    local status
    status=$(curl -s -o /dev/null -w '%{http_code}' -u "$MANUTENCAO" -X POST "$BASE_URL/" \
        -H 'Content-Type: application/json' -d "{\"username\":\"$1\",\"senha\":\"$2\"}")
    echo "POST /             $1 -> $status"
}

desbloquear() {
    local status
    status=$(curl -s -o /dev/null -w '%{http_code}' -u "$ADMIN" -X PUT "$BASE_URL/unlock/$1")
    echo "PUT /unlock/$1 -> $status"
}

echo "== cadastro de usuários (ROLE_MANUTENCAO) =="
for u in ana bruno carla diego; do cadastrar "$u" "${u}123"; done

echo "== rodada 1: logins válidos =="
for u in joao maria pedro ana bruno; do login "$u" "${u}123"; done
sleep $PAUSA

echo "== rodada 2: senha errada, usuário inexistente e bloqueio da carla =="
login joao errada
login fulano qualquer
login carla errada
login carla errada
sleep $PAUSA

echo "== rodada 3: mais logins válidos e bloqueio do pedro =="
for i in 1 2 3; do login maria maria123; login ana ana123; done
login joao joao123
login pedro errada
login pedro errada
login pedro pedro123
sleep $PAUSA

echo "== rodada 4: bloqueio do diego e desbloqueio da carla (ROLE_ADMIN) =="
login diego errada
login diego errada
desbloquear carla
login carla carla123
sleep $PAUSA

echo "== rodada 5 =="
for i in 1 2 3 4; do login bruno bruno123; done
login hacker 123456
login admin admin123

echo "== situação final (GET / com ROLE_MANUTENCAO) =="
curl -s -u "$MANUTENCAO" "$BASE_URL/"
echo
