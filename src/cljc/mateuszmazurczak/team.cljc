(ns mateuszmazurczak.team)

(def founders
  [{:id 1
    :name "Anthony Caumond"
    :img "/img/team/anthonycaumond.jpeg"
    :title :hephaistox-co-founder
    :linkedin "https://www.linkedin.com/in/anthony-caumond-a365b15/"
    :description :anthony-description}
   {:id 2
    :name "Mateusz Mazurczak"
    :img "/img/team/mateuszmazurczak.png"
    :title :hephaistox-co-founder
    :linkedin "https://www.linkedin.com/in/mateuszmazurczak/"
    :description :mati-description}])

(def teammates
  [{:id 1
    :name "Alain Didier"
    :img "/img/team/alaindidier.jpeg"
    :hidden? true
    :title :engineer-architect-warehouse-transport-manufacture
    :linkedin "https://www.linkedin.com/in/alain-didier-14333a76/"
    :description :alain-description
    :hephaistox-role :alain-hephaistox-description
    :hephaistox-title :process-software-engineer
    :moonshot-subjects [{:title :warehouse-managment
                         :description :alain-warehouse-description}
                        {:title :transportation-managment
                         :description :alain-transportation-description}]}
   {:id 2
    :name "Ali Abdessamad"
    :img "/img/team/aliabdessamad.jpeg"
    :linkedin "https://www.linkedin.com/in/ali-abdessamad-55381762/"
    :title :supply-chain-operational
    :description :ali-description
    :hephaistox-role :ali-hephaistox-description
    :hephaistox-title :consultant}
   {:id 3
    :name "Alice Gosserez"
    :img "/img/team/alicegosserez.jpg"
    :linkedin
    "https://www.linkedin.com/in/ganjez-accompagnement-et-développement-2a7986220/"
    :title :process-qa-engineer
    :description :alice-description
    :moonshot-subjects [{:title :digitalization
                         :description :alice-digitalization-description}]}
   {:id 4
    :name "Michel Batisse"
    :img "/img/team/michelbatisse.jpeg"
    :linkedin "https://www.linkedin.com/in/michel-batisse-49728a5/"
    :title :supply-chain-expert
    :hidden? true
    :description :michel-description
    :hephaistox-title :supply-chain-expert
    :hephaistox-role :michel-hephaistox-description}
   {:id 7
    :name "Laurent Verdier"
    :img "/img/team/laurentverdier.jpeg"
    :linkedin "https://www.linkedin.com/in/laurent-verdier-2797181a/"
    :title :it-integrator
    :description :laurent-description
    :hephaistox-role :laurent-hephaistox-description
    :hephaistox-title :it-integrator}
   {:id 8
    :name "Nikolay Tchernev"
    :img "/img/team/nikolaytchernev.jpeg"
    :linkedin "https://perso.isima.fr/~nitchern/"
    :title :university-professor
    :description :nikolay-description
    :hephaistox-role :nikolay-hephaistox-description
    :hephaistox-title :scientific-advisor
    :moonshot-subjects [{:title :erp
                         :description :nikolay-erp-description}
                        {:title :discrete-event-symulation
                         :description
                         :nikolay-discret-event-symulation-description}]}
   {:id 9
    :name "Philippe Lacomme"
    :img "/img/team/philippelacomme.jpeg"
    :linkedin "https://perso.isima.fr/~lacomme/index_anc.php"
    :title :optimization-quantuum-researcher
    :hidden? true
    :description :philippe-description
    :hephaistox-role :philippe-hephaistox-description
    :hephaistox-title :optimization-quantuum-scientific-advisor
    :moonshot-subjects [{:title :quantuum
                         :description :philippe-quantuum-description}]}
   {:id 10
    :name "Pierre Didier"
    :hidden? true
    :img "/img/team/pierredidier.jpeg"
    :linkedin "https://www.linkedin.com/in/pierre-didier-475848135/"
    :title :engineer-architect-qa
    :description :pierre-description
    :hephaistox-role :pierre-hephaistox-description
    :hephaistox-title :engineer-architect-qa
    :moonshot-subjects [{:title :manufacturing-bill-material
                         :description
                         :pierre-manufacturing-bill-material-description}
                        {:title :qa-4
                         :description :pierre-qa-4-description}
                        {:title :design-cost
                         :description :pierre-design-cost-description}]}
   {:id 11
    :name "Arek Flinik"
    :img "/img/team/arekflinik.jpeg"
    :linkedin "https://www.linkedin.com/in/arekflinik/"
    :title :ai-cto
    :description :arek-description
    :hephaistox-role :arek-hephaistox-description
    :hephaistox-title :ai-nlp-expert
    :moonshot-subjects [{:title :ai
                         :description :arek-ai-description}]}])
