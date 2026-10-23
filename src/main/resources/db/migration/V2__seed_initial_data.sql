-- V2__seed_initial_data.sql
-- Educational Content, Quiz Questions, Options, and Announcements for EduConnect

-- 1. Insert Categories
INSERT INTO categories (id, name, slug, description) VALUES
('11111111-1111-1111-1111-111111111111', 'Comunicación Asertiva', 'comunicacion-asertiva', 'Habilidades para expresar ideas, sentimientos y necesidades de manera directa, honesta y respetuosa.')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO categories (id, name, slug, description) VALUES
('22222222-2222-2222-2222-222222222222', 'Escucha Activa', 'escucha-activa', 'Técnicas fundamentales para comprender verdaderamente a los interlocutores sin emitir juicios precipitados.')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO categories (id, name, slug, description) VALUES
('33333333-3333-3333-3333-333333333333', 'Resolución de Conflictos', 'resolucion-de-conflictos', 'Estrategias de mediación, negociación constructiva y retroalimentación positiva en entornos académicos y laborales.')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO categories (id, name, slug, description) VALUES
('44444444-4444-4444-4444-444444444444', 'Comunicación Digital y No Verbal', 'comunicacion-digital-no-verbal', 'Netiqueta, lenguaje corporal y gestión de la interacción en plataformas virtuales de aprendizaje.')
ON CONFLICT (slug) DO NOTHING;

-- 2. Insert Articles
INSERT INTO articles (id, category_id, title, slug, excerpt, content, author, read_time_minutes) VALUES
(
    'a1111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'El Arte de la Asertividad: Comunicar sin Agredir ni Someterse',
    'el-arte-de-la-asertividad',
    'Aprende a diferenciar el estilo asertivo de la pasividad y la agresividad para defender tus derechos y proyectos académicos con convicción y respeto.',
    '# El Arte de la Asertividad: Comunicar sin Agredir ni Someterse\n\nLa **asertividad** es la capacidad de expresar nuestros pensamientos, deseos y sentimientos de manera directa, sincera y adecuada, considerando los derechos de los demás.\n\n### Los Tres Estilos de Comunicación\n\n1. **Estilo Pasivo**: La persona posterga sus propias necesidades para evitar conflictos, lo cual genera frustración acumulada.\n2. **Estilo Agresivo**: Intenta imponer su perspectiva mediante la intimidación o descalificación, erosionando la confianza del equipo.\n3. **Estilo Asertivo**: Comunica posturas con claridad y firmeza, promoviendo el diálogo constructivo y el mutuo respeto.\n\n### Técnicas Prácticas de Asertividad\n\n- **El Disco Rayado**: Reiterar con serenidad tu postura o límite frente a insistencias indebidas.\n- **El Banco de Niebla**: Aceptar parte de la crítica sin renunciar a tu criterio fundamental.\n- **Mensajes en Primera Persona ("Mensaje Yo")**: Describe la situación objetiva ("Cuando se entrega el informe fuera de plazo...") en lugar de atacar al individuo ("Tú siempre fallas...").\n\n*La asertividad no garantiza convencer al otro, pero sí salvaguarda la dignidad personal y la profesionalidad.*',
    'Ing. Dennis Vera & Equipo EduConnect',
    6
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO articles (id, category_id, title, slug, excerpt, content, author, read_time_minutes) VALUES
(
    'a2222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222222',
    'Escucha Activa: El Superpoder del Trabajo en Equipo',
    'escucha-activa-superpoder-equipo',
    'Descubre cómo el hábito de escuchar atentamente sin interrumpir transforma las dinámicas grupales y eleva la calidad técnica en proyectos de desarrollo.',
    '# Escucha Activa: El Superpoder del Trabajo en Equipo\n\nOír es un proceso fisiológico involuntario; **escuchar activamente** es un acto consciente de voluntad, atención y empatía hacia quien emite el mensaje.\n\n### Principales Barreras de la Escucha en el Aula\n\n- Preparar mentalmente la réplica antes de que el compañero termine su argumento.\n- Distracciones digitales (notificaciones, pestañas abiertas durante reuniones técnicas).\n- Filtros cognitivos o prejuicios acerca de la competencia del interlocutor.\n\n### Pilares de la Escucha Activa\n\n- **Parafrasear**: Repetir con tus propias palabras el mensaje central ("Si entiendo bien, propones refactorizar este módulo antes de integrar la API...").\n- **Preguntas Clarificadoras**: Despejar dudas específicas para verificar la comprensión exacta de los requerimientos.\n- **Validación Emocional**: Reconocer el esfuerzo y la perspectiva ajena antes de emitir contrapropuestas técnicas.',
    'EduConnect Academic Mentorship',
    5
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO articles (id, category_id, title, slug, excerpt, content, author, read_time_minutes) VALUES
(
    'a3333333-3333-3333-3333-333333333333',
    '33333333-3333-3333-3333-333333333333',
    'Feedback Constructivo: Cómo Criticar Ideas sin Lastimar Personas',
    'feedback-constructivo-guia',
    'Aprende el modelo de retroalimentación constructiva para revisiones de código, presentaciones de proyectos y evaluaciones colaborativas.',
    '# Feedback Constructivo: Cómo Criticar Ideas sin Lastimar Personas\n\nEn entornos de desarrollo de software y formación superior, la retroalimentación continua es el motor de la excelencia técnica.\n\n### La Fórmula del Feedback Efectivo\n\n1. **Específico y Orientado al Comportamiento**: Señala eventos concretos con ejemplos verificables, nunca juicios sobre la personalidad.\n2. **Técnica del Sándwich o Giro Constructivo**: Inicia destacando un aspecto positivo sólido, profundiza en el área de mejora con sugerencias aplicables, y concluye con una perspectiva motivadora.\n3. **Foco en el Futuro (Feedforward)**: Menos énfasis en lamentar el error pasado y mayor enfoque en cómo prevenirlo en el siguiente ciclo o sprint.',
    'Lic. Patricia Cáceres',
    7
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO articles (id, category_id, title, slug, excerpt, content, author, read_time_minutes) VALUES
(
    'a4444444-4444-4444-4444-444444444444',
    '44444444-4444-4444-4444-444444444444',
    'Netiqueta y Comunicación Asincrónica en Equipos de Software',
    'netiqueta-comunicacion-asincronica',
    'Buenas prácticas de comunicación escrita en repositorios Git, issues, chats de equipo y foros virtuales educativos.',
    '# Netiqueta y Comunicación Asincrónica en Equipos de Software\n\nEl desarrollo colaborativo moderno depende primordialmente de canales asincrónicos (Git commits, pull requests, mensajes en foros y chats).\n\n### Reglas Clave de Netiqueta Profesional\n\n- **Mensajes Contextualizados**: Al reportar un fallo o plantear una consulta, provee contexto, pasos de reproducción y logs de error.\n- **Tono Profesional y Amable**: La ausencia de inflexión vocal en el texto plano puede percibirse erróneamente como rudeza; sé explícito y cortés.\n- **Claridad Sintética**: Utiliza encabezados, viñetas y formato Markdown para facilitar la lectura rápida de tus colegas.',
    'Equipo EduConnect',
    4
)
ON CONFLICT (slug) DO NOTHING;

-- 3. Insert Quiz Questions and Options
-- Question 1
INSERT INTO quiz_questions (id, prompt, explanation, order_index, is_active) VALUES
(
    'c1111111-1111-1111-1111-111111111111',
    'Durante una reunión de equipo, un compañero presenta una idea técnica que consideras inviable. ¿Cuál es la respuesta asertiva?',
    'La comunicación asertiva reconoce el aporte del compañero y expone argumentos objetivos sin emitir juicios despectivos.',
    1,
    TRUE
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO quiz_options (id, question_id, option_text, is_correct, points) VALUES
('d1111111-1111-1111-1111-111111111101', 'c1111111-1111-1111-1111-111111111111', 'Interrumpirlo de inmediato para decirle que su idea no funcionará nunca.', FALSE, 0),
('d1111111-1111-1111-1111-111111111102', 'c1111111-1111-1111-1111-111111111111', 'Escuchar su propuesta completa, agradecer su aporte y compartir datos objetivos sobre los riesgos técnicos.', TRUE, 25),
('d1111111-1111-1111-1111-111111111103', 'c1111111-1111-1111-1111-111111111111', 'Quedarte en silencio para no generar conflicto, aunque sepas que el proyecto fracasará.', FALSE, 5),
('d1111111-1111-1111-1111-111111111104', 'c1111111-1111-1111-1111-111111111111', 'Burlarte discretamente en el chat privado con otros integrantes.', FALSE, 0)
ON CONFLICT (id) DO NOTHING;

-- Question 2
INSERT INTO quiz_questions (id, prompt, explanation, order_index, is_active) VALUES
(
    'c2222222-2222-2222-2222-222222222222',
    '¿Cuál de las siguientes acciones representa una verdadera Escucha Activa?',
    'Parafrasear valida que la interpretación coincide con la intención original del interlocutor.',
    2,
    TRUE
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO quiz_options (id, question_id, option_text, is_correct, points) VALUES
('d2222222-2222-2222-2222-222222222201', 'c2222222-2222-2222-2222-222222222222', 'Pensar en tu respuesta mientras la otra persona aún está hablando.', FALSE, 0),
('d2222222-2222-2222-2222-222222222202', 'c2222222-2222-2222-2222-222222222222', 'Parafrasear lo que dijo para confirmar la comprensión y hacer preguntas pertinentes.', TRUE, 25),
('d2222222-2222-2222-2222-222222222203', 'c2222222-2222-2222-2222-222222222222', 'Asentir con la cabeza mecánicamente mientras revisas tu teléfono celular.', FALSE, 0),
('d2222222-2222-2222-2222-222222222204', 'c2222222-2222-2222-2222-222222222222', 'Cambiar de tema bruscamente cuando termine su intervención.', FALSE, 0)
ON CONFLICT (id) DO NOTHING;

-- Question 3
INSERT INTO quiz_questions (id, prompt, explanation, order_index, is_active) VALUES
(
    'c3333333-3333-3333-3333-333333333333',
    'Al formular una crítica constructiva sobre un informe o código de un compañero, ¿cuál es la mejor práctica?',
    'El feedback constructivo se centra en el producto o acción específica y ofrece alternativas viables de mejora.',
    3,
    TRUE
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO quiz_options (id, question_id, option_text, is_correct, points) VALUES
('d3333333-3333-3333-3333-333333333301', 'c3333333-3333-3333-3333-333333333333', 'Decir "este trabajo no sirve, hazlo de nuevo".', FALSE, 0),
('d3333333-3333-3333-3333-333333333302', 'c3333333-3333-3333-3333-333333333333', 'Enfocarte en hechos concretos, resaltar lo positivo y sugerir soluciones prácticas de mejora.', TRUE, 25),
('d3333333-3333-3333-3333-333333333303', 'c3333333-3333-3333-3333-333333333333', 'No decir nada y rehacer el trabajo tú solo en la madrugada.', FALSE, 5),
('d3333333-3333-3333-3333-333333333304', 'c3333333-3333-3333-3333-333333333333', 'Quejarte públicamente ante el docente sin haber hablado antes con el compañero.', FALSE, 0)
ON CONFLICT (id) DO NOTHING;

-- Question 4
INSERT INTO quiz_questions (id, prompt, explanation, order_index, is_active) VALUES
(
    'c4444444-4444-4444-4444-444444444444',
    'En un canal de comunicación asincrónica (ej. Slack, Discord o foros), ¿cuál hábito favorece la colaboración eficaz?',
    'Escribir mensajes claros, autocontenidos y con contexto reduce la fricción y acelera la resolución de dudas.',
    4,
    TRUE
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO quiz_options (id, question_id, option_text, is_correct, points) VALUES
('d4444444-4444-4444-4444-444444444401', 'c4444444-4444-4444-4444-444444444444', 'Escribir solo "Hola" y esperar 10 minutos a que respondan antes de formular tu consulta.', FALSE, 0),
('d4444444-4444-4444-4444-444444444402', 'c4444444-4444-4444-4444-444444444444', 'Enviar mensajes estructurados con saludo, contexto del problema, pasos intentados y la solicitud precisa.', TRUE, 25),
('d4444444-4444-4444-4444-444444444403', 'c4444444-4444-4444-4444-444444444444', 'Escribir TODO EN MAYÚSCULAS para que noten tu urgencia.', FALSE, 0),
('d4444444-4444-4444-4444-444444444404', 'c4444444-4444-4444-4444-444444444444', 'Enviar capturas de pantalla borrosas tomadas con el celular a la pantalla sin ninguna explicación.', FALSE, 0)
ON CONFLICT (id) DO NOTHING;

-- 4. Insert Announcements
INSERT INTO announcements (id, title, content, author_name, author_role, priority) VALUES
(
    'b1111111-1111-1111-1111-111111111111',
    'Convocatoria Abierta: Actividad de Desarrollo Web III 2026',
    'Se recuerda a todos los estudiantes de Sistemas Informáticos del turno nocturno que la entrega oficial del proyecto temático vence el 22 de Octubre.',
    'Lic. Patricia Cáceres',
    'Docente Titular Web III',
    'URGENT'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO announcements (id, title, content, author_name, author_role, priority) VALUES
(
    'b2222222-2222-2222-2222-222222222222',
    'Taller de Comunicación Asertiva en Equipos de TI',
    'Este viernes a las 19:30 se llevará a cabo la sesión práctica sobre resolución asertiva de discrepancias en pull requests y retrospectivas ágiles.',
    'Ing. Dennis Vera',
    'Facilitador Académico',
    'NORMAL'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO announcements (id, title, content, author_name, author_role, priority) VALUES
(
    'b3333333-3333-3333-3333-333333333333',
    'Repositorio y Swagger API Disponibles',
    'La API REST desarrollada con Scala 3, ZIO 2 y sttp Tapir se encuentra completamente documentada en /docs con soporte OpenAPI 3.0.',
    'EduConnect Core Team',
    'Equipo de Desarrollo',
    'INFO'
)
ON CONFLICT (id) DO NOTHING;
