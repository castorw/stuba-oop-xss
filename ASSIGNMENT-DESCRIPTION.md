Ľubomír Kaplán, xkaplanl, PKSS 13

eXpress Security Simulator (XSS)
Aplikácia Express Security Simulator (ďalej len XSS) bude jednoduchý simulátor štandardného zabezpečovacieho systému použitého pri zabezpečovaní budov a exteriérových priestorov.
XSS sa bude fungovať ako klient-server aplikácia. Obidve časti tohoto programu budú využívať ešte 2 pomocné knižnice obsahujúce zdieľané triedy, rozhrania, atď.
Vrámci serverovej časti bude spustený server pre správu systému (Management Server) a taktiež jadro bezpečnostného systému (XSS Engine). Engine zabezpečuje integritu senzorov a prijíma informácie o zmenách stavu. Obsahuje taktiež generátor náhodných udalostí (Random Event Generator), ktorý náhodne generuje poplachové udalosť v závislosti od konfigurácie jednotlivých senzorov. Dostupný bude simulátor pohybového (PIR) senzoru, simulátor detektoru dymu, simulátor teplotného senzoru. Systém bude taktiež schopný spolupracovať s fyzickými zariadeniami (senzormi), ktoré spĺňajú komunikačný protokol (Physical Sensor Protocol). Bude implementovaný pohybový senzor.
Klientská časť (XSS Console) slúži ako vzdialená konzola pre správu systému pokrývajúca plnú funkcionalitu - zobrazenie stavu systému, konfigurácia systému, konfigurácia senzorov, atď.


Splnenie kritérií:
•	Program obsahuje zmysluplné dedenie medzi vlastnými triedami.
•	Program obsahuje prekonávanie vlasných metód vrámci triedy aj pri dedení.
•	Program obsahuje zapúzdrenie aj agregáciu.
•	Všetky podstatné časti projektu sú dostatočne okomentované.
•	Program používa návrhový vzor „client-server“
•	Kód je organizovaný do balíkov
•	Mimoriadne stavy sú ošetrené
•	Program obsahuje grafické rozhranie (klientskú konzolu)
•	Program používa Refletions API a Collections Framework
•	Program obsahuje multi-threading
•	Program využíva „Reflections 0.9.9“ pre získanie zoznamu balíkov
