<h1 align="center">Plaza de comidas</h1>

# Microservicio Plazoleta

<img src="https://img.shields.io/badge/%E2%98%95%20Java-%23c98524.svg?style=logoColor=white" alt="Logo Java" />
<img src="https://img.shields.io/badge/-MySQL-005C84?style=flat-square&logo=mysql&logoColor=black" alt="Logo Mysql" />
<img src="https://img.shields.io/badge/Swagger-%2385EA2D.svg?&style=flat-square&logo=swagger&logoColor=blue" alt="Logo Swagger" />
<img src="https://img.shields.io/badge/Spring%20Security-%23569A31.svg?&style=flat-square&logo=spring&logoColor=white" alt="Logo Spring Security" />
<img src="https://img.shields.io/badge/Amazon%20RDS%20MariaDB-%23FF9900.svg?&style=flat-square&logo=amazonaws&logoColor=white&color=FF9900" alt="Amazon RDS MariaDB" />


## Descripción

Plazoleta es un microservicio encargado de administrar restaurantes, platos y pedidos. Este documento describe los endpoints disponibles y la autenticación requerida para acceder a ellos. El acceso a los endpoints requiere un token de autenticación válido correspondiente a uno de los siguientes roles: ADMINISTRADOR, PROPIETARIO, EMPLEADO o CLIENTE.

## Autenticación

Para autenticarse en cada solicitud, debes incluir un token de autenticación válido en el encabezado de autorización utilizando el esquema "Bearer ".

    Authorization: Bearer <TOKEN>

Reemplaza <TOKEN> con tu token de autenticación válido, cuando iniciaste sesion se obtiene este token.

## Endpoints

<ul> 
    <li>Crear restaurante.</li>
    <li>Crear platos.</li>
    <li>Modificar el plato sus campos precio y descripcion.</li>
    <li>Hablitar o deshabilitar el plato.</li>
    <li>Listar los restaurantes.</li>
    <li>Listar platos activos de un restaurante agrupados por categoria.</li>
    <li>Realizar pedido.</li>
    <li>Cancelar pedido.</li>
    <li>Listar pedidos por sus estados.</li>
    <li>Asignarse a pedido como Chef.</li>
    <li>Notificar que el pedido está listo.</li>
    <li>Entregar pedido.</li>
</ul>

## Ejecutar

1. Empaquetar microservicio usando el comando en la raiz del proyecto
`./gradlew build
`
2. Ejecutar microservicio
`java -jar ./build/libs/plazoleta-1.0.jar
`
## Sobre Pruebas de Mutacion con PITEST

Es una herramienta que nos ayuda a evaluar la calidad de las pruebas unitarias. 
PITEST introduce cambios deliberados (mutaciones) en el código para verificar si las pruebas existentes detectan estos cambios.

Evaluación de Resultados:
* Si las pruebas fallan debido a la mutación, el mutante es "asesinado", lo cual es deseable.
* Si las pruebas pasan a pesar de la mutación, el mutante "sobrevive", lo que indica que las pruebas pueden no ser suficientemente robustas.

PITEST genera un informe que muestra qué mutantes fueron asesinados y cuáles sobrevivieron, proporcionando métricas sobre la efectividad de las pruebas unitarias.

### Ejecuta PITEST

Usa el comando:
`./gradlew pitest
`

Busca los resultados en la siguiente direccion: `/build/reports/pitest/`

<img src="https://drive.google.com/uc?export=view&id=1WAx-MPkGaO-vz5LD2zau57DmY9_fl9ql" alt="Ejemplo del informe generado por pitest" width="400" height="300">

## Siguiente Microservicio <a href="https://github.com/JaiderMartinez/backend_power_mensajeria.git">Mensajeria</a>