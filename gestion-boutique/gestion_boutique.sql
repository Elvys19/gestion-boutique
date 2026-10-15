--
-- PostgreSQL database dump
--

\restrict YqbYegSCPU7dwU9cLuheyFDgBydBuTsmBAfNyWuuitM0ocNu4TNXQu6ReixAyjn

-- Dumped from database version 16.10
-- Dumped by pg_dump version 16.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id integer NOT NULL,
    nom character varying(100) NOT NULL
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categories_id_seq OWNER TO postgres;

--
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;


--
-- Name: fournisseurs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fournisseurs (
    id integer NOT NULL,
    nom character varying(150) NOT NULL,
    telephone character varying(30),
    adresse character varying(255)
);


ALTER TABLE public.fournisseurs OWNER TO postgres;

--
-- Name: fournisseurs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.fournisseurs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fournisseurs_id_seq OWNER TO postgres;

--
-- Name: fournisseurs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.fournisseurs_id_seq OWNED BY public.fournisseurs.id;


--
-- Name: ligne_livraison; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligne_livraison (
    id integer NOT NULL,
    livraison_id integer NOT NULL,
    produit_id integer NOT NULL,
    quantite integer NOT NULL,
    prix_achat numeric(12,2) NOT NULL
);


ALTER TABLE public.ligne_livraison OWNER TO postgres;

--
-- Name: ligne_livraison_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ligne_livraison_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ligne_livraison_id_seq OWNER TO postgres;

--
-- Name: ligne_livraison_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ligne_livraison_id_seq OWNED BY public.ligne_livraison.id;


--
-- Name: ligne_vente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligne_vente (
    id integer NOT NULL,
    vente_id integer NOT NULL,
    produit_id integer NOT NULL,
    quantite integer NOT NULL,
    prix_unitaire numeric(12,2) NOT NULL,
    sous_total numeric(12,2) NOT NULL
);


ALTER TABLE public.ligne_vente OWNER TO postgres;

--
-- Name: ligne_vente_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ligne_vente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ligne_vente_id_seq OWNER TO postgres;

--
-- Name: ligne_vente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ligne_vente_id_seq OWNED BY public.ligne_vente.id;


--
-- Name: livraisons; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.livraisons (
    id integer NOT NULL,
    date_livraison timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fournisseur_id integer,
    user_id integer
);


ALTER TABLE public.livraisons OWNER TO postgres;

--
-- Name: livraisons_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.livraisons_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.livraisons_id_seq OWNER TO postgres;

--
-- Name: livraisons_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.livraisons_id_seq OWNED BY public.livraisons.id;


--
-- Name: produits; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produits (
    id integer NOT NULL,
    nom character varying(150) NOT NULL,
    prix_achat numeric(12,2) NOT NULL,
    prix_vente numeric(12,2) NOT NULL,
    stock integer DEFAULT 0 NOT NULL,
    stock_minimum integer DEFAULT 5,
    categorie_id integer
);


ALTER TABLE public.produits OWNER TO postgres;

--
-- Name: produits_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.produits_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.produits_id_seq OWNER TO postgres;

--
-- Name: produits_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.produits_id_seq OWNED BY public.produits.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    nom character varying(100) NOT NULL,
    prenom character varying(100) NOT NULL,
    matricule character varying(50) NOT NULL,
    sexe character varying(10),
    date_naissance date,
    identifiant character varying(50) NOT NULL,
    mot_de_passe character varying(255) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: ventes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ventes (
    id integer NOT NULL,
    date_vente timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    client_nom character varying(150),
    user_id integer,
    total numeric(12,2) DEFAULT 0
);


ALTER TABLE public.ventes OWNER TO postgres;

--
-- Name: ventes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ventes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ventes_id_seq OWNER TO postgres;

--
-- Name: ventes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ventes_id_seq OWNED BY public.ventes.id;


--
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- Name: fournisseurs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseurs ALTER COLUMN id SET DEFAULT nextval('public.fournisseurs_id_seq'::regclass);


--
-- Name: ligne_livraison id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_livraison ALTER COLUMN id SET DEFAULT nextval('public.ligne_livraison_id_seq'::regclass);


--
-- Name: ligne_vente id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vente ALTER COLUMN id SET DEFAULT nextval('public.ligne_vente_id_seq'::regclass);


--
-- Name: livraisons id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraisons ALTER COLUMN id SET DEFAULT nextval('public.livraisons_id_seq'::regclass);


--
-- Name: produits id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produits ALTER COLUMN id SET DEFAULT nextval('public.produits_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: ventes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ventes ALTER COLUMN id SET DEFAULT nextval('public.ventes_id_seq'::regclass);


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories (id, nom) FROM stdin;
\.


--
-- Data for Name: fournisseurs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.fournisseurs (id, nom, telephone, adresse) FROM stdin;
1	Grossiste central	74102297	bobo dioulasso
2	Steve service	01020304	france
\.


--
-- Data for Name: ligne_livraison; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ligne_livraison (id, livraison_id, produit_id, quantite, prix_achat) FROM stdin;
1	1	1	100	12500.00
2	2	1	10	12500.00
3	3	3	1	1600000.00
4	3	2	10	1500000.00
\.


--
-- Data for Name: ligne_vente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ligne_vente (id, vente_id, produit_id, quantite, prix_unitaire, sous_total) FROM stdin;
1	1	1	6	15000.00	90000.00
2	2	1	11	15000.00	165000.00
3	3	1	1	15000.00	15000.00
4	4	3	10	1700000.00	17000000.00
5	4	2	10	16000000.00	160000000.00
6	5	3	20	1700000.00	34000000.00
7	5	2	10	16000000.00	160000000.00
8	6	3	10	1700000.00	17000000.00
9	7	3	10	1700000.00	17000000.00
\.


--
-- Data for Name: livraisons; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.livraisons (id, date_livraison, fournisseur_id, user_id) FROM stdin;
1	2026-10-10 01:17:35.954871	1	\N
2	2026-10-10 12:25:54.660432	2	\N
3	2026-10-12 02:01:42.078054	2	5
\.


--
-- Data for Name: produits; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.produits (id, nom, prix_achat, prix_vente, stock, stock_minimum, categorie_id) FROM stdin;
1	Riz	12500.00	15000.00	112	5	\N
3	huile	1600000.00	1700000.00	11	5	\N
2	poulet	1500000.00	16000000.00	20	5	\N
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, nom, prenom, matricule, sexe, date_naissance, identifiant, mot_de_passe) FROM stdin;
2	BAMOGO	Samiratou	MAT-002	Féminin	2004-12-27	Samiratou	PBKDF2$210000$ncMcfWRYffCrx4cdYuXFIA==$MfYpXHgMX5LYGNCEVD8rES1KuEJ4IU8rIwiUNwMH17A=
5	SANOU	Elvys	MAT-003	Masculin	2004-11-08	Brulon	PBKDF2$210000$Ya+IpAjLhlKl6ikM0NqmbQ==$7HEr8hvLEHuD2FGTEij9qcsMX3fz2AMeWUdcQNO4V90=
\.


--
-- Data for Name: ventes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ventes (id, date_vente, client_nom, user_id, total) FROM stdin;
1	2026-10-09 18:59:10.043476	elvys	\N	90000.00
2	2026-10-09 19:00:01.084686	ddd	\N	165000.00
3	2026-10-09 19:03:28.804176	rrr	\N	15000.00
4	2026-10-10 12:11:38.45535	\N	\N	177000000.00
5	2026-10-11 20:18:50.975075	steve	\N	194000000.00
6	2026-10-11 22:40:55.105266	iii	5	17000000.00
7	2026-10-12 01:41:04.364306	nnn	5	17000000.00
\.


--
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categories_id_seq', 1, false);


--
-- Name: fournisseurs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fournisseurs_id_seq', 2, true);


--
-- Name: ligne_livraison_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ligne_livraison_id_seq', 4, true);


--
-- Name: ligne_vente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ligne_vente_id_seq', 9, true);


--
-- Name: livraisons_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.livraisons_id_seq', 3, true);


--
-- Name: produits_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.produits_id_seq', 3, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 5, true);


--
-- Name: ventes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ventes_id_seq', 7, true);


--
-- Name: categories categories_nom_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_nom_key UNIQUE (nom);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: fournisseurs fournisseurs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseurs
    ADD CONSTRAINT fournisseurs_pkey PRIMARY KEY (id);


--
-- Name: ligne_livraison ligne_livraison_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_livraison
    ADD CONSTRAINT ligne_livraison_pkey PRIMARY KEY (id);


--
-- Name: ligne_vente ligne_vente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vente
    ADD CONSTRAINT ligne_vente_pkey PRIMARY KEY (id);


--
-- Name: livraisons livraisons_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraisons
    ADD CONSTRAINT livraisons_pkey PRIMARY KEY (id);


--
-- Name: produits produits_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produits
    ADD CONSTRAINT produits_pkey PRIMARY KEY (id);


--
-- Name: users users_identifiant_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_identifiant_key UNIQUE (identifiant);


--
-- Name: users users_matricule_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_matricule_key UNIQUE (matricule);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: ventes ventes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ventes
    ADD CONSTRAINT ventes_pkey PRIMARY KEY (id);


--
-- Name: ligne_livraison ligne_livraison_livraison_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_livraison
    ADD CONSTRAINT ligne_livraison_livraison_id_fkey FOREIGN KEY (livraison_id) REFERENCES public.livraisons(id) ON DELETE CASCADE;


--
-- Name: ligne_livraison ligne_livraison_produit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_livraison
    ADD CONSTRAINT ligne_livraison_produit_id_fkey FOREIGN KEY (produit_id) REFERENCES public.produits(id);


--
-- Name: ligne_vente ligne_vente_produit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vente
    ADD CONSTRAINT ligne_vente_produit_id_fkey FOREIGN KEY (produit_id) REFERENCES public.produits(id);


--
-- Name: ligne_vente ligne_vente_vente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_vente
    ADD CONSTRAINT ligne_vente_vente_id_fkey FOREIGN KEY (vente_id) REFERENCES public.ventes(id) ON DELETE CASCADE;


--
-- Name: livraisons livraisons_fournisseur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraisons
    ADD CONSTRAINT livraisons_fournisseur_id_fkey FOREIGN KEY (fournisseur_id) REFERENCES public.fournisseurs(id);


--
-- Name: livraisons livraisons_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraisons
    ADD CONSTRAINT livraisons_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: produits produits_categorie_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produits
    ADD CONSTRAINT produits_categorie_id_fkey FOREIGN KEY (categorie_id) REFERENCES public.categories(id);


--
-- Name: ventes ventes_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ventes
    ADD CONSTRAINT ventes_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

\unrestrict YqbYegSCPU7dwU9cLuheyFDgBydBuTsmBAfNyWuuitM0ocNu4TNXQu6ReixAyjn

