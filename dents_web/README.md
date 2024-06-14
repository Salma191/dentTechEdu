# Projet Dents

1. Pour récupérer notre image backend, utilisez : `docker pull mohjamoutawadii1164/pfa2024:backend`
2. Créez un réseau : `docker network create mohja`
3. Lancez SQL : `docker run --name mysql-container --network mohja -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=dents -d mysql:latest`
4. Lancez phpMyAdmin : `docker run --name phpmyadmin-container --network mohja -d --link mysql-container:db -p 8084:80 phpmyadmin`
5. Lancez l'application : `docker run --name backend-container --net mohja -p 5050:5050 --link mysql-container:mysql -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dents -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=root mohjamoutawadii1164/pfa2024:backend`. Cela créera automatiquement un utilisateur dans votre base de données, mais vous devez accéder à votre base de données en utilisant http://localhost:8084/ (root pour le nom d'utilisateur et le mot de passe) allez dans la table admin -> insert -> et choisissez l'ID de l'utilisateur créé.
6. Accédez à l'application en utilisant localhost:5050/login.
8. Nom d'utilisateur admin : admin
9. Mot de passe admin : 1234
