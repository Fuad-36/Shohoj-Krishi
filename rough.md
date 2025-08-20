Objective

You will annotate conversations between users and an AI assistant, judging whether the assistant's final response correctly matches the language, script, and dialect expected by the user — all crucial for training a granular Language Identification (LID) model.

🧩 Annotation Categories

Each item will ask you to fill in these three language sections based on the last turn:

Prompt Language(s) — Languages in the user’s final prompt.

Response Language(s) — Languages used in the assistant’s final response.

Expected Language(s) – codes the user wanted in the reply.

IMPORTANT: Override volume with intent: explicit or implicit user preferences always take precedence over word‑count ratios.

For each:

Select Language Code (ISO 639, e.g., pt, hi)

Select Script Code ONLY (ISO 15924, e.g., Latn, Deva) for non-Latin languages - This field will only be enabled if a non Latn script language is selected.

Select Locale/Region Code (ISO 3166-1, e.g., BR, IN) if evident, otherwise use XX.

Example format: hi_Deva_IN or pt_BR

🧭 Core Principles

🧑‍💻 User Experience & Intent

User intent rules

Look at the latest prompt first, then check established patterns in prior turns.

Multi-turn language switches are not considered code-switching. This means if a user switches languages over several turns, it's not the same as code-switching within a single turn.

Primary / Secondary language detection

To determine the primary language of a prompt and response, count the words in each; the language with the most words is the primary.

The expected languages are determined by the intent, so they might not be in order of volume.

Language-match logic

A "match" means all expected language codes are present in the response, regardless of their frequency.

Code-switching (single-turn only)

The dominant language leads. If the languages are balanced, mirror the user's mix.

Do not respond only in a new language unless the user specifically asks for it.

🔁 Steerability

Maintain consistency with previous user turns.

Honor implicit language/script/dialect preferences based on prior context.

Romanized script usage implies informality — don’t use it in formal contexts unless explicitly requested.

🧪 Examples

Scenario

Narrow Match

Broad Match

Reason

Prompt in hi_Latn_IN, response in hi_Deva_IN

❌

❌

User did not request Devanagari

Prompt in emoji only, but conversation is in English

✅

✅

Inferred from context

Prompt ambiguous between es and pt, model replies in es

✅

✅

Acceptable match

Request to translate something to Malay, no text provided

✅

✅

No translation needed yet

Code-switching (id + English word)

✅

✅

Acceptable bilingual mix

🧪 Edge Cases Examples

Scenario

Match?

Key Rule

History in ES; final prompt in EN; model replies in ES

✅

Expectation set by history → ES expected

Same case but model replies in EN

✅

EN allowed because final prompt is 100 % EN & no “stay in ES” request

User repeatedly asked “in Hindi” earlier; last prompt neutral; model answers in Hindi (hi_Deva_IN)

✅

Prior explicit request governs expected language

User asks Italian→English translation; model replies mostly Italian with short EN phrase

✅

EN expected, but Italian volume OK; match judged on presence

🧾 Code-Switching Handling

If one language is dominant: prefer that.

If balanced: mirror the user’s mix in the response.

Don’t respond only in the secondary language unless requested.

⛔ Rejection Guidelines

✅ Valid Rejection Reasons

Adult Content

Foreign Language

Multiple Speakers (more than 2)

Content Contains PII

Sensitive Content

Explicit/Inappropriate Content

Rater Lacks Required Expertise

Model Output Is Truncated

Blank Content

Incoherent or Unintelligible Content

Requests Unsupported Model Capability (Image Generation)

Requests Unsupported Model Capability (Real Time Information)

Requests Unsupported Model Capability (Search)

Requests Unsupported Model Capability (Other)

Other (Please Specify)

Scope: Apply rejection checks only to the last prompt + response, unless earlier turns contain disqualifying content (e.g., PII).

Valid rejection reasons (use any label – “Adult,” “Sensitive,” “Explicit” are interchangeable):

Adult / Sensitive / Explicit content

Foreign language (only if unidentifiable after using detection tools)

PII present, incoherent text, truncated output, blank content, unsupported capability reques


# Tips
Always scan earlier turns if the last exchange seems linguistically inconsistent; prior instructions often dictate the correct language, script, or dialect.

The Expected Languages list may contain multiple codes when the user routinely uses or anticipates more than one language.

When a user says they will provide text to translate but hasn’t yet, keep expectations in the current conversation language until actual source text arrives.

A single prompt in a different language doesn’t force an immediate switch; consider which language has dominated the dialogue and respond accordingly.

Obvious misspellings or near‑misses should be interpreted in context rather than treated as unknown.

Mirroring the dominant user language and script remains the safest default unless the user clearly requests otherwise.

Introducing an extra, unrequested language counts as a mismatch—even if the primary target language is included.

Rank languages as Primary or Secondary by how much text each occupies in the response, not by importance or formality.

In mixed‑language scenarios, including brief source‑language snippets alongside the translation is acceptable and often expected.



Assessment Question #2
Respond the following questions based on the provided Chat History:

Prompt

Investiguen sobre los parques industriales de Pilar, Zárate y Campana. Identifiquen si son privados, estatales o mixtos, así como su anti-güedad, dimensión, las empresas radicadas y las ventajas ofrecidas. ¿ A qué factores responde la localización? Pueden ayudarse con el Google Maps. Compárenlos y elaboren un informe.

Response

Informe sobre Parques Industriales en Pilar, Zárate y Campana

Parque Industrial Pilar

Ubicación: A 5 kilómetros de la ciudad de Pilar

Antigüedad: Creado en 1976

Dimensión: 920 hectáreas

Empresas radicadas: Más de 200 empresas, con alrededor de 30.000 personas trabajando diariamente

Rubros mayoritarios: Industria Química (23%), Alimentos y Bebida (20%) y Caucho y Plástico (18%)

Ventajas: Ubicación estratégica, infraestructura desarrollada, servicios básicos y avanzados disponibles

Gestión: Privada, aunque no se especifica si es mixta o completamente privada

Parque Industrial Los Libertadores (Campana)

Ubicación: En el km 77,5 de la Ruta Nacional 9, Campana, Provincia de Buenos Aires

Antigüedad: En desarrollo, sin fecha específica de inauguración

Dimensión: 450 hectáreas

Empresas radicadas: En desarrollo, sin empresas mencionadas específicamente

Ventajas: Doble acceso directo por Ruta 6 y Ruta 9, cercanía al Puerto de Campana y a la Capital Federal

Gestión: Privada, desarrollado por Grupo Bautec

Zárate

No se encontraron detalles específicos sobre un parque industrial en Zárate que se pueda comparar directamente con los mencionados en Pilar y Campana. Sin embargo, se menciona que los partidos de Escobar, Campana, Zárate y Pilar cuentan con importantes parques industriales.

Factores de Localización

La localización de estos parques industriales responde a factores como:

Accesibilidad: Cerca de rutas nacionales y provinciales importantes, facilitando el transporte de mercaderías y la movilidad de trabajadores.

Proximidad a puertos y centros urbanos: Ventajoso para la logística y el acceso a servicios y mano de obra.

Disponibilidad de servicios básicos y avanzados: Energía eléctrica, agua, gas, telefonía y fibra óptica son fundamentales para el funcionamiento de las empresas.

Infraestructura desarrollada: Calles pavimentadas, alumbrado público y seguridad son aspectos importantes para la operación de las empresas.

Comparación

El Parque Industrial Pilar es el más grande de Argentina y uno de los más importantes en América Latina, con una amplia trayectoria y una gran cantidad de empresas radicadas.

El Parque Industrial Los Libertadores en Campana es un proyecto en desarrollo con una ubicación estratégica y ventajas competitivas.

La zona de Campana-Zárate-Pilar es reconocida por su atractivo para la instalación de industrias debido a su infraestructura y servicios. 32

Prompt

Explicar proceso de privatización en Argentina 1989

Response

Proceso de Privatización en Argentina en 1989

El proceso de privatización en Argentina en 1989 se llevó a cabo durante el gobierno de Carlos Menem y fue parte de un conjunto de reformas económicas y políticas destinadas a liberalizar la economía y reducir el papel del Estado en la producción de bienes y servicios.

Contexto

A fines de la década de 1980, Argentina enfrentaba una grave crisis económica caracterizada por alta inflación, déficit fiscal y una economía estancada. El gobierno de Menem implementó un programa de reformas económicas que incluía la privatización de empresas estatales.

Objetivos

Los objetivos del proceso de privatización fueron:

Reducir el déficit fiscal mediante la venta de empresas estatales

Aumentar la eficiencia y la productividad en sectores clave de la economía

Atraer inversión extranjera y estimular el crecimiento económico

Proceso

El proceso de privatización se llevó a cabo de la siguiente manera:

Ley de Reforma del Estado: En 1989, se aprobó la Ley de Reforma del Estado, que estableció el marco legal para la privatización de empresas estatales.

Selección de empresas: Se seleccionaron varias empresas estatales para ser privatizadas, incluyendo empresas de servicios públicos como teléfonos, electricidad y gas, así como empresas industriales y de transporte.

Venta de empresas: Las empresas seleccionadas se vendieron a empresas privadas nacionales y extranjeras a través de diferentes modalidades, como subastas públicas y negociaciones directas.

Concesiones: En algunos casos, se otorgaron concesiones a empresas privadas para la prestación de servicios públicos, como la gestión de carreteras y puertos.

Impacto

El proceso de privatización tuvo un impacto significativo en la economía argentina:

Reducción del déficit fiscal: La venta de empresas estatales ayudó a reducir el déficit fiscal y a estabilizar la economía.

Aumento de la inversión extranjera: La privatización atrajo inversión extranjera y estimuló el crecimiento económico.

Mejora de la eficiencia: La privatización llevó a una mayor eficiencia y productividad en sectores clave de la economía.

Sin embargo, el proceso de privatización también tuvo críticas y controversias:

Pérdida de control estatal: La privatización llevó a una pérdida de control estatal sobre sectores estratégicos de la economía.

Desigualdad: La privatización benefició principalmente a las empresas privadas y a los sectores más ricos de la sociedad, mientras que los sectores más pobres y vulnerables se vieron afectados negativamente.

Corrupción: El proceso de privatización estuvo marcado por acusaciones de corrupción y falta de transparencia.

En resumen, el proceso de privatización en Argentina en 1989 fue un intento de liberalizar la economía y reducir el papel del Estado en la producción de bienes y servicios. Aunque tuvo algunos impactos positivos, también generó críticas y controversias que aún se debaten en la actualidad.

Prompt

Dejo de haber empresas estatales?

Response

Empresas Estatales en Argentina después de la Privatización de 1989

Aunque el proceso de privatización en Argentina en 1989 llevó a la venta de muchas empresas estatales, no todas las empresas estatales desaparecieron. Algunas empresas estatales siguieron existiendo y otras fueron creadas posteriormente.

Ejemplos de Empresas Estatales que Siguen Existiendo

YPF: Aunque en 1993 se privatizó parcialmente, en 2012 el gobierno argentino expropió el 51% de las acciones de YPF que estaban en manos de Repsol.

Ferrocarriles Argentinos: Aunque se privatizó gran parte del sistema ferroviario, algunas líneas y servicios siguen siendo operados por empresas estatales.

Administración General de Puertos: La empresa estatal que gestiona los puertos de Argentina.

Energía Argentina S.A. (ENARSA) : La empresa estatal que se encarga de la exploración y producción de hidrocarburos.

Nuevas Empresas Estatales

ARSAT: La empresa estatal que se encarga de la gestión de los satélites de comunicaciones de Argentina.

AySA: La empresa estatal que se encarga de la prestación de servicios de agua potable y saneamiento en la provincia de Buenos Aires.

En resumen, aunque el proceso de privatización en Argentina en 1989 llevó a la venta de muchas empresas estatales, no todas las empresas estatales desaparecieron y algunas siguen existiendo y operando en la actualidad.

Prompt

1 km son 1000metros?

Response

¡Exacto! 1 kilómetro (km) es igual a 1000 metros (m). Esta es una conversión básica y fundamental en el sistema métrico decimal.

¿Necesitas ayuda con alguna otra conversión o cálculo?

Prompt

Cual es el objetivo del conductismo

Response

Objetivo del Conductismo

El objetivo principal del conductismo es estudiar la conducta observable y medible de los seres humanos y animales, con el fin de entender y predecir su comportamiento.

Enfoque

El conductismo se enfoca en:

Conducta observable: Se centra en la conducta que se puede observar y medir directamente, en lugar de en procesos mentales internos como pensamientos o sentimientos.

Aprendizaje: Estudia cómo la conducta se aprende y se modifica a través de la experiencia y el entorno.

Condicionamiento: Investiga cómo los estímulos y las consecuencias influyen en la conducta.

Aplicaciones

El conductismo tiene aplicaciones en diversas áreas, como:

Psicología: Ayuda a entender y tratar trastornos del comportamiento.

Educación: Informa sobre métodos de enseñanza y aprendizaje efectivos.

Terapia: Se utiliza en terapias conductuales para modificar conductas problemáticas.

En resumen, el conductismo busca comprender y predecir la conducta observable, con el fin de mejorar nuestra comprensión del comportamiento humano y animal.

Prompt

1000 kilometros son 100 metros

Response

No, 1000 kilómetros no son 100 metros.

Conversión correcta

1 kilómetro (km) = 1000 metros (m)

Por lo tanto:

1000 kilómetros (km) = 1.000.000 metros (m)

¿Necesitas ayuda con alguna otra conversión o cálculo?

Prompt

Debido a la bolsa de wallstreet fue que argentina comenzó en la década de 1930 una industrialización de sustitución de importaciones?

Response

La Crisis de 1929 y la Industrialización por Sustitución de Importaciones (ISI) en Argentina

La crisis de 1929, también conocida como la Gran Depresión, tuvo un impacto significativo en la economía argentina. Sin embargo, la industrialización por sustitución de importaciones (ISI) en Argentina comenzó como respuesta a la crisis, pero no fue directamente causada por la bolsa de Wall Street.

Causas y Factores

Crisis de 1929: La crisis global afectó negativamente las exportaciones argentinas, especialmente de productos agrícolas, lo que llevó a una disminución de las reservas de divisas y a una contracción económica.

Deterioro de los Términos de Intercambio: La caída de los precios internacionales de los productos primarios afectó la economía argentina, que dependía en gran medida de la exportación de bienes agrícolas.

Proteccionismo y Políticas Económicas: En respuesta a la crisis, muchos países, incluido Argentina, adoptaron políticas proteccionistas para proteger sus industrias nacionales. Esto incluyó la implementación de aranceles y otras barreras comerciales.

La Industrialización por Sustitución de Importaciones (ISI)

La ISI en Argentina se intensificó durante la década de 1930 y continuó en las décadas siguientes. El objetivo era reducir la dependencia de las importaciones y fomentar el desarrollo industrial nacional. Esto implicó la implementación de políticas para promover la industria local, como:

Protección Arancelaria: Aumento de aranceles para proteger a las industrias nacionales de la competencia extranjera.

Inversión en Infraestructura y Servicios: Mejora de la infraestructura y los servicios para apoyar el crecimiento industrial.

Políticas de Fomento Industrial: Implementación de políticas específicas para fomentar el desarrollo de ciertas industrias.

Conclusión

Si bien la crisis de 1929 no fue la causa directa de la industrialización por sustitución de importaciones en Argentina, sí fue un factor importante que aceleró y profundizó este proceso. La respuesta a la crisis incluyó la adopción de políticas proteccionistas y el fomento de la industria nacional como estrategia para enfrentar los desafíos económicos.

Prompt

lo que entendí es que medio fue un factor pero también fue por la demanda de bienes interno

Response

La Industrialización por Sustitución de Importaciones (ISI) en Argentina

La industrialización por sustitución de importaciones (ISI) en Argentina se debió a una combinación de factores, incluyendo:

Crisis de 1929: La crisis global y el deterioro de los términos de intercambio afectaron negativamente las exportaciones argentinas y llevaron a una disminución de las reservas de divisas.

Demanda interna de bienes: La demanda interna de bienes y servicios en Argentina también jugó un papel importante en el impulso de la ISI. A medida que la economía argentina crecía, la demanda de bienes y servicios aumentaba, y la industria nacional comenzó a producir bienes que anteriormente se importaban.

La interacción entre factores externos e internos

La interacción entre la crisis global y la demanda interna de bienes y servicios en Argentina creó un entorno propicio para la ISI. La crisis global limitó las importaciones, lo que llevó a la industria nacional a producir bienes que anteriormente se importaban para satisfacer la demanda interna.

Políticas económicas

Las políticas económicas implementadas por el gobierno argentino también jugaron un papel importante en la promoción de la ISI. Estas políticas incluyeron:

Proteccionismo: La implementación de aranceles y otras barreras comerciales para proteger a la industria nacional.

Inversión en infraestructura y servicios: La mejora de la infraestructura y los servicios para apoyar el crecimiento industrial.

En resumen, la ISI en Argentina fue el resultado de una combinación de factores externos e internos, incluyendo la crisis global, la demanda interna de bienes y servicios, y las políticas económicas implementadas por el gobierno.

Prompt

Cuento de borges que habla sobre un circulo

Response

El Círculo en la Obra de Jorge Luis Borges

Uno de los cuentos más famosos de Jorge Luis Borges que involucra un círculo es "La Biblioteca de Babel". Aunque no se centra exclusivamente en un círculo, el concepto de lo infinito y lo cíclico es fundamental en este relato.

"La Biblioteca de Babel"

En este cuento, Borges describe una biblioteca que contiene todos los libros posibles de 410 páginas, cada uno con todas las combinaciones posibles de letras y símbolos. La biblioteca es infinita y cíclica, ya que eventualmente se repiten los mismos libros.

El Círculo y la Infinitud

El concepto de círculo en la obra de Borges a menudo se relaciona con la idea de lo infinito y lo cíclico. En "La Biblioteca de Babel", el círculo representa la infinitud y la repetición eterna de los mismos patrones y combinaciones.

Otros Cuentos

Aunque "La Biblioteca de Babel" es uno de los ejemplos más destacados, Borges explora temas similares en otros cuentos, como "El Aleph" y "La Muerte y la Brújula". En estos relatos, Borges utiliza metáforas y símbolos para explorar conceptos filosóficos y metafísicos.

En resumen, el círculo en la obra de Borges es un símbolo poderoso que representa la infinitud, la repetición y la cíclicidad. Sus cuentos siguen siendo objeto de estudio y reflexión en la literatura y la filosofía.

Prompt

Esta bien? La Plusvalía es la diferencia entre el salario del trabajador y el valor de su producción

Response

La Plusvalía según Karl Marx

La plusvalía es un concepto clave en la teoría económica de Karl Marx. Según Marx, la plusvalía es la diferencia entre el valor que el trabajador produce y el valor que recibe en forma de salario.

Definición

La plusvalía se genera cuando el trabajador produce un valor mayor que el valor de su fuerza de trabajo, es decir, el valor que recibe en forma de salario. Esta diferencia es apropiada por el capitalista, quien la considera como ganancia.

Ejemplo

Supongamos que un trabajador produce mercancías por un valor de 100 unidades monetarias en una jornada laboral, pero su salario es de 50 unidades monetarias. En este caso, la plusvalía sería de 50 unidades monetarias (100 - 50).

Importancia

La plusvalía es importante en la teoría de Marx porque:

Explotación: Marx argumenta que la plusvalía es una forma de explotación del trabajador por parte del capitalista, ya que el trabajador no recibe el valor total de su producción.

Acumulación de capital: La plusvalía es la fuente de la acumulación de capital, ya que los capitalistas utilizan la plusvalía para invertir y expandir su capital.

En resumen, la plusvalía es la diferencia entre el valor producido por el trabajador y el valor que recibe en forma de salario, y es un concepto clave en la teoría económica de Marx.

Prompt

Will you be leaving school by the age of 19 or will you have left school by the age of 19?

Response

Diferencia entre "Will you be leaving school" y "Will you have left school" \n\n\nLa diferencia entre estas dos preguntas radica en el momento en que se enfoca la acción de dejar la escuela.\n\n\n* "Will you be leaving school by the age of 19?" : Esta pregunta se refiere a si la acción de dejar la escuela estará ocurriendo o se estará llevando a cabo antes o en el momento de cumplir 19 años. Enfoca el proceso de dejar la escuela en relación con la edad específica.\n* "Will you have left school by the age of 19?" : Esta pregunta se refiere a si la acción de dejar la escuela ya estará completada para cuando se cumplan 19 años. Enfoca el resultado de haber dejado la escuela antes de alcanzar esa edad.\n\n\nEn resumen:\n\n\n* La primera pregunta ("Will you be leaving school...") se centra en el momento de dejar la escuela.\n* La segunda pregunta ("Will you have left school...") se centra en si ya se ha dejado la escuela para una fecha específica.
