# OpenConsent

OpenConsent es un proyecto orientado a la creación de un protocolo abierto de consentimiento para aplicaciones móviles, diseñado para implementar requisitos de consentimiento válidos según GDPR mediante un enfoque verificable, transparente y centrado en la persona usuaria.

## Descripción

El proyecto surge como respuesta al problema de los flujos de consentimiento opacos o manipulativos en aplicaciones móviles, donde muchas personas aceptan tratamientos de datos sin comprender realmente qué autorizan, con qué finalidad y bajo qué condiciones. OpenConsent propone una base técnica reutilizable para que las aplicaciones integren experiencias de consentimiento más claras, auditables y fáciles de revocar. [file:3][file:4]

La propuesta combina patrones de UX, registros verificables, estructura de eventos legible por máquina y componentes reutilizables que facilitan tanto el cumplimiento normativo como la trazabilidad del consentimiento a lo largo del tiempo.

## Objetivo

El objetivo general del proyecto es desarrollar una solución abierta para gestionar consentimiento móvil de forma clara, específica, informada, inequívoca y revocable, permitiendo a desarrolladores, organizaciones y auditores contar con una base común para demostrar cumplimiento y ofrecer mayor control al usuario final.

## Problema que aborda

En el ecosistema móvil actual, muchas aplicaciones implementan mecanismos de consentimiento que dificultan el rechazo, presentan información fragmentada o utilizan patrones oscuros para influir en la decisión de los usuarios. Esto genera riesgos de incumplimiento normativo y reduce la capacidad real de las personas para controlar sus datos personales.

OpenConsent busca cerrar la brecha entre los principios legales del GDPR y su implementación técnica real dentro de aplicaciones móviles.

## Alcance del proyecto

OpenConsent contempla una solución compuesta por varios elementos:

- Una especificación abierta para modelar decisiones de consentimiento.
- SDKs de referencia para plataformas móviles como Android.
- Flujos de interfaz que evitan patrones oscuros y favorecen decisiones informadas.
- Un historial de consentimientos verificable y fácil de consultar.
- Mecanismos de auditoría y trazabilidad para evidenciar accountability.

## Estructura del repositorio

La estructura puede evolucionar conforme avance el desarrollo, pero actualmente el repositorio contiene la base del proyecto Android y los archivos de configuración necesarios para su construcción.

Ejemplo general esperado:

```text
OpenConsent/
├── app/
├── gradle/
├── openconsent/
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

## Tecnologías previstas

Según la propuesta del proyecto, OpenConsent contempla componentes para Android, además de una capa de visualización o auditoría para revisar historiales de consentimiento y validar eventos registrados.

En esta versión del repositorio, el proyecto ya incluye una base Android estructurada con Gradle.

## Principios de diseño

OpenConsent se apoya en varios principios clave:

- Transparencia desde el diseño.
- Consentimiento libre, específico, informado e inequívoco.
- Revocación sencilla y trazable.
- Interoperabilidad con ecosistemas existentes.
- Neutralidad frente a proveedores y enfoque open source.

## Estado del proyecto

El repositorio representa una base inicial del desarrollo práctico. La visión del proyecto incluye una especificación estable, SDKs de referencia, visor de auditoría e integración piloto en aplicaciones móviles reales. 

## Cómo clonar el proyecto

```bash
git clone https://github.com/fernandohiguitabe-del/OpenConsent.git
cd OpenConsent
```

## Cómo abrir el proyecto

1. Abrir Android Studio.
2. Seleccionar **Open**.
3. Elegir la carpeta del repositorio clonado.
4. Esperar a que Gradle sincronice las dependencias.


## Autores

- Fernando de Jesús Higuita Betancur
- Juliana Stefhany Rivera Suarez

## Licencia

OpenSource
