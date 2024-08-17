#_{:heph-ignore {:css true}}
(ns mateuszmazurczak.ui.generated.simulation)

(defn notion-generated-simulation-en
  []
  [:article {:id "4687ef4b-f543-47b8-93bb-0d7a75f0e054"
             :class "page sans"}
   [:header
    [:h1 {:class "page-title"}
     "What is simulation?"]
    [:p {:class "page-description"}]]
   [:div {:class "page-body"}
    [:div
     [:p {:id "0c6ca4e6-72b1-4090-b8ff-a7850e682176"
          :class ""}
      "Simulation is a technique that mimics a real system - and simplifies it, to learn something useful about it."]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "When to use simulation?"]
      [:div {:class "indented"}
       [:p {:id "c0f87c6d-fe6c-4544-a9fd-901395a56020"
            :class ""}
        "Simulation is useful when you have a question about a system, and testing assumptions on the real system is not possible, because :"]
       [:div {:class "indented"}
        [:ul {:id "010cbbd5-1b9f-4042-bfdd-7d288b541e28"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Your system does not exist yet,"]]
        [:ul {:id "ad918ddf-bae4-4e05-b379-c169d8ea7c4a"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "The test is risky regarding the customers, the environment, …;"]]
        [:ul {:id "1e6cdc74-c145-4c7d-842f-fc2d639b6e8f"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Doing the change is too expansive to be tested,"]]
        [:ul {:id "ed36a6f2-7949-46fb-8daa-d8e6635d3c64"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "The test is destructive (you have no way to undo your change if you’re not happy with it)"]]]
       [:p]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Simulation technique"]
      [:div {:class "indented"}
       [:p {:id "ca3394d5-1948-4b15-bc7f-95dd3ccf3792"
            :class ""}
        "The table below shows different simulation techniques, these are completely different ways to evaluate a potential answer of a system. Note that all of them do not apply to a plant in the context of the supply chain."]
       [:div {:class "indented"}
        [:figure {:class "block-color-gray_background callout"
                  :style {"whiteSpace" "pre-wrap"
                          "display" "flex"}
                  :id "34f1603c-9bea-4a97-a9cb-3e63141767a0"}
         [:div {:style {"fontSize" "1.5em"}}
          [:span {:class "icon"}
           "💡"]]
         [:div {:style {"width" "100%"}}
          "Simulation is an old technique, most of the examples below have been used intensively since the early twentieth century."]]
        [:table {:id "a6be2fdd-3fb0-4520-9491-645b8e37bfaa"
                 :class "simple-table"}
         [:thead {:class "simple-table-header"}
          [:tr {:id "9ed355f1-a280-4e1e-b8cb-2d449ff80cae"}
           [:th {:id "UznU"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "185px"}}
            "Some simulation technique"]
           [:th {:id "OScO"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "349px"}}
            "Description"]
           [:th {:id "<sa:"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "578px"}}
            "Examples"]]]
         [:tbody
          [:tr {:id "15690432-d062-466e-9857-181f4e75f62c"}
           [:th {:id "UznU"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "185px"}}
            "Computer program"]
           [:td {:id "OScO"
                 :class ""
                 :style {"width" "349px"}}
            "Simulation software: 3D modeling, discrete event simulation, mesh physical simulation, …"]
           [:td {:id "<sa:"
                 :class ""
                 :style {"width" "578px"}}
            "3D modeling: Design the gears of an engine" [:br]
            "Discrete event simulation: Decrease the stock level in your workshop"
            [:br]
            "Mesh physical simulation: Deformation of the shield of an artificial satellite during its entry into the atmosphere."
            [:br]]]
          [:tr {:id "445afb8c-66bd-4803-89d2-f99c063bb3e0"}
           [:th {:id "UznU"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "185px"}}
            "Experiments in wind tunnel"]
           [:td {:id "OScO"
                 :class ""
                 :style {"width" "349px"}}
            "Build an object with a clay model"]
           [:td {:id "<sa:"
                 :class ""
                 :style {"width" "578px"}}
            "A clay car model is useful for understanding airflow"]]
          [:tr {:id "761a3384-dda0-45c1-ab82-b635f5aea613"}
           [:th {:id "UznU"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "185px"}}
            "A lower-scale model"]
           [:td {:id "OScO"
                 :class ""
                 :style {"width" "349px"}}
            "A duplication of the system at a lower scale"]
           [:td {:id "<sa:"
                 :class ""
                 :style {"width" "578px"}}
            "A model of a building before its construction to make a rendering"]]
          [:tr {:id "2a660180-398e-40ed-ad70-6c82c851cd4b"}
           [:th {:id "UznU"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "185px"}}
            "Prototype"]
           [:td {:id "OScO"
                 :class ""
                 :style {"width" "349px"}}
            "Close to the studied system as the real one but done with simpler cheaper resources"]
           [:td {:id "<sa:"
                 :class ""
                 :style {"width" "578px"}}
            "Create a car prototype to meet customers and gather their feedback in a car show,"]]
          [:tr {:id "d58052fa-e847-4e59-8419-6e49103a7bd3"}
           [:th {:id "UznU"
                 :class "simple-table-header-color simple-table-header"
                 :style {"width" "185px"}}
            "Calculous"]
           [:td {:id "OScO"
                 :class ""
                 :style {"width" "349px"}}
            "Mathematical formal calculation"]
           [:td {:id "<sa:"
                 :class ""
                 :style {"width" "578px"}}
            "The trajectory of a rocket before sending it to the moon"]]]]]
       [:p]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Simulation for Supply Chain"]
      [:div {:class "indented"}
       [:p {:id "86ca048e-fd5e-479c-8f35-ea3c48f5c040"
            :class ""}
        "Supply Chains can leverage simulation to ease decision-making. Our offer is about using a computer program technique, called discrete event simulation, which is especially adapted for the question arising in the industry."]
       [:p {:id "56b0a5d8-ca63-4fc0-8b5b-bcbb8fb21edb"
            :class ""}
        "Discrete event simulation can mimic your plant and its evolution over time. In marketing and literature, the word digital twin is often used. This is a way to tell that you will do some experiments on that twin instead of modifying your real-life system to learn something useful."]
       [:p {:id "cd741424-ab9b-4456-9dba-fb153c611154"
            :class ""}
        "Simulation requires some toolings to reproduce the evolution over time, as Excel sheets are most often dealing with average values and static views. Together with your experts’ intuitions, this is what you use to make decisions and it’s most of the time efficient."]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "How to work with simulation?"]
      [:div {:class "indented"}
       [:p {:id "de180113-1479-47bb-9230-79d4800f00f0"
            :class ""}
        "Simulation is a scientific approach, which is described in the diagram below:"]
       [:figure {:id "428770a7-33dc-4b52-832d-0282bf6856ec"
                 :class "image"
                 :style {"textAlign" "center"}}
        [:a
         {:href
          "What%20is%20simulation%204687ef4bf54347b893bb0d7a75f0e054/Untitled.png"}
         [:img
          {:style {"width" "432px"}
           :src
           "What%20is%20simulation%204687ef4bf54347b893bb0d7a75f0e054/Untitled.png"}]]]
       [:ul {:id "aeb6cac1-e585-4cef-8861-632be67b25b8"
             :class "toggle"}
        [:li
         [:details {:open ""}
          [:summary "Description"]
          [:p {:id "70ece204-dea4-4a5d-ac0a-d37364993682"
               :class ""}
           "In the text below"
           [:code "words written like that"]
           "are referring to the box with the same text in the diagram."]
          [:p {:id "3a9b9d36-3a62-4a8e-a026-7f8fe760f102"
               :class ""}
           "The story starts with:"]
          [:ul {:id "1ddc80ce-3ced-44d0-bdb3-18666e8a028d"
                :class "bulleted-list"}
           [:li {:style {"listStyleType" "disc"}}
            "a"
            [:code "real system"]]]
          [:ul {:id "b735a2c4-5bf4-4426-9633-335472efaae4"
                :class "bulleted-list"}
           [:li {:style {"listStyleType" "disc"}}
            "a “"
            [:code "what-if"]
            "” question (e.g.What if the throughput is increased?)."]]
          [:p {:id "87aa5fd2-52ab-4933-8778-2b7c7deffe02"
               :class ""}
           "Then we do a system analysis and create an as simple as possible"
           [:code "model"]
           "that will be help to answer that question."
           [:code "Your system’s data"]
           "are shaping the model we create, as some data may not be available, or not precise enough."]
          [:p {:id "7cc209b7-e64b-4313-85d1-b5db2c37acd9"
               :class ""}
           "The"
           [:code "Model"]
           "is then"
           [:code "implemented"]
           "in a simulation model. What we call"
           [:code "simulate"]
           "- which is the simulation model execution- outputs a trace of the execution and performance indicator values."
           [:code "Indicators"]
           "are needed to evaluate the likelihood of the solution and its performance."]
          [:p {:id "2b72f8ce-8dcd-4940-9087-db461e49a4bb"
               :class ""}
           "Then, the"
           [:code "valid"]
           "ation is about comparing the scenario and its expected effect, its likeliness. To validate it, it is often compared to other scenario, shown to some experts to have as much as possible confidence."]
          [:p {:id "f0f4b0ab-ef80-4d57-9536-ac63ccab708c"
               :class ""}
           [:del [:br] [:br]]
           "If the model we used is not"
           [:code "valid"]
           "ated, the model will be updated and the whole simulation loop started again."]
          [:p {:id "03e50147-b7f9-4f42-a71a-13928e921777"
               :class ""}]
          [:p {:id "f7ff0261-e886-4e1a-87f2-c3b37ca03ed4"
               :class ""}
           "When the decision is"
           [:code "valid"]
           "ated, then, an"
           [:code "implementation"]
           "could be applied and the real system updated."]]]]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Simulation is for complex systems."]
      [:div {:class "indented"}
       [:p {:id "dfa1a1c6-29da-4f26-b61f-49e9649b67ef"
            :class ""}
        "A"
        [:a {:href "https://en.wikipedia.org/wiki/Complex_system"}
         "complex system"]
        "is composed with some components which may interact with each other. Their behavior is intrinsically difficult to model due to the dependencies, competitions, relationships, or other types of interactions between their parts or between a given system and its environment."]
       [:p {:id "7db8969a-c220-469e-94a2-79bd01783bf3"
            :class ""}
        "Well known examples are ant colonies, ecological systems, climate, …. and also plants are known to be complex systems."]
       [:p {:id "a0e38428-1f81-4e28-bd44-fa0f7625d92d"
            :class ""}
        "In your day to day, it means that - even if some parts are well defined and well understood - the whole workshop, the whole plant may become challenging to understand or have unexpected behaviors. More likely you have this feeling that you know your plant, you have intuitions about how it works, but sometimes its behavior is unexpected. The more the situation is new and the more this may happen."]
       [:p {:id "efdb9f5a-34cf-41a7-adea-86155a00c8aa"
            :class ""}
        "When that uncertainty happens, the consequences on your plant are:"]
       [:ul {:id "dbd1d683-97ab-4056-be0c-f612db448c70"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "you add some delays,"]]
       [:ul {:id "9c4ba584-07db-4b4a-b4e1-87d4f13ad6fc"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "you create overstock,"]]
       [:ul {:id "164d7565-8555-4dac-a2da-47341aef1c77"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "or accept the risks"]]
       [:p {:id "0a8de566-fdb0-4bca-bd9c-1a6c55787408"
            :class ""}
        "To cope with that consequences, simulation is a crucial tooling that will turn intuition to actuals. With detailed informations, it is quite easy to align all the stakeholders on the facts demonstrated in a simulation. So decision taking becomes much less subjective and much more based on data."]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "When to use discrete event simulation?"]
      [:div {:class "indented"}
       [:p {:id "9a47d8b7-dca3-476e-a122-41889b4a470f"
            :class ""}
        "Generally speaking, simulation can be used to answer questions concerning your plant starting with “What if ?”"]
       [:p {:id "92ac126f-4287-4c90-94ef-cf1c0175f0d7"
            :class ""}
        "For instance:"]
       [:div {:class "indented"}
        [:ul {:id "77edf94f-6d18-41d8-a211-d791826a4728"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "What if the throughput is increased?"]]
        [:ul {:id "4605b99a-b7b6-4865-b28d-f8b057f3bd46"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "What if more work in progress is authorized?"]]
        [:ul {:id "b6d42a64-173e-4a33-baac-94bbd0704b5c"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "What if the mix of products of customers is evolving?"]]
        [:ul {:id "34718c7e-63b1-4363-844b-0e604e832d9c"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "What if this new product is industrialized in the same workshop?"]]
        [:ul {:id "27dd14bb-e6e9-44f3-b9d6-da2f789a58ac"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "What if we add a new machine to the workshop?"]]]
       [:p]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Discrete event simulation properties"]
      [:div {:class "indented"}
       [:ul {:id "21045ae0-57c0-465f-b81d-ae5f66e5882f"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "White box approach"
         [:ul {:id "b212fd9b-d3bf-4fe2-938e-abb65c620753"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "In a white-box approach, every aspect of the simulation can be comprehensively explained and verified."]]
         [:ul {:id "c28e45b3-8a65-4749-81fc-92b66b5dc45a"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "This makes simulation adaptive (easy to modify, and update)"]]
         [:ul {:id "86ce2588-bf5c-42a2-9e88-9d4b53898572"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "The content within this &quot;white box&quot; represents the depth of your expert knowledge."]]
         [:ul {:id "6dede067-964b-466c-bd7b-ecb716c45944"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "This stands in contrast to the black box approach, where only inputs and outputs can be understood"]]]]
       [:ul {:id "1e7b0a56-c792-4c1a-9fd4-33682fc03aef"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Evolutionary"
         [:ul {:id "4e4e4b47-121f-47a0-9b6a-ff2c14954d67"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "It starts simple and becomes more complex. You start with a simple model and you validate it, add more complexity to it"]]
         [:ul {:id "906f9381-40d9-482a-a51e-dfdd316116ea"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "This helps to understand, explain and validate the model"]]]]
       [:ul {:id "93881991-1dc6-4943-8f88-fc6d31fb40b8"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Dynamic"
         [:ul {:id "5cb55c96-183a-4786-b6c8-91f94cde4ee4"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Simulation models the time and the evolution of the system over time"]]]]
       [:ul {:id "b8f7ba1f-4bed-4346-9757-d8837786a560"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Discrete"
         [:ul {:id "643c7be3-2916-43be-ad54-90c4f3a444b4"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Variables change at distinct points in time"]]]]
       [:h3 {:id "a998a99b-0e3a-4c5d-a7f4-51e9334fc7d8"
             :class ""}
        [:strong "If required by the “what if” question, it may also include:"]]
       [:ul {:id "f24f0c7d-1377-4f35-bece-abc444c3e8d0"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Stochastic"
         [:ul {:id "262eda99-2f31-425b-8360-49df25e3d3fc"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Some data is replaced with random variables if the physical system is stochastic, or to simplify a phenomenon that is too complicated to model."]]
         [:ul {:id "02c134af-c69f-40db-85a9-002acbd1bfab"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Each scenario is evaluated many times, and each evaluation uses different values for defined random variables."]]
         [:ul {:id "c345368e-9ca0-4c9e-9905-08a83d3cefc8"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Results are often summed up with their average, standard deviation, minimum, and maximum solutions"]]]]
       [:ul {:id "5c0b0381-ca9a-442e-9399-9126abd48a21"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Robustness evaluation"
         [:ul {:id "255c72f3-724d-42a1-8e5e-383279cb443f"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Simulation can evaluate (thanks to the"
           [:a {:href "https://en.wikipedia.org/wiki/Monte_Carlo_method"}
            "Monte Carlo method"]
           ") the robustness of a solution regarding some input data changes. For instance, a new policy can be more robust than the one currently used in the plant regarding product mix changes."]]
         [:ul {:id "575cd958-00c4-4d8a-a70c-f48657f8edcb"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Robustness is useful for you to:"
           [:ul {:id "32dae8d6-74ff-4837-a422-4ca3fbd03fc8"
                 :class "bulleted-list"}
            [:li {:style {"listStyleType" "square"}}
             "wonder if a policy is efficient even if the demand mix is changing."]]
           [:ul {:id "7f332730-7e52-424c-9ec9-1efdef8983e2"
                 :class "bulleted-list"}
            [:li {:style {"listStyleType" "square"}}
             "if the scheduling is efficient even if the duration of the operation is not deterministic."]]]]
         [:ul {:id "0465ffb8-17e0-4719-bc73-700d63d80ffe"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Most often robustness is studied, with a probabilistic approach."]]
         [:ul {:id "6fab15a3-dcc7-4101-a0ba-75c26b73026c"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "The robustness study explores numerous use cases."]]]]]]]]])

(defn notion-generated-simulation-fr
  []
  [:article {:id "6445c723-624a-4a16-9fb2-90fce0d54b75"
             :class "page sans"}
   [:header
    [:h1 {:class "page-title"}
     "Qu’est-ce-que la simulation"]
    [:p {:class "page-description"}]]
   [:div {:class "page-body"}
    [:div
     [:p {:id "1f843df8-b8a6-43b7-8a4f-fb64ce232a2c"
          :class ""}
      "La simulation est une technique utilisée pour reproduire le comportement d’un système réel, en le simplifiant, pour apprendre quelque chose d’utile à son propos."]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Quand utiliser la simulation?"]
      [:div {:class "indented"}
       [:p {:id "cd33d7e3-8e85-4f98-857f-de10c75aa0ab"
            :class ""}
        "La simulation est utile si vous avez des questions à propos de votre système et que tester ces hypothèses sur le système réel n’est pas possible ou pas souhaitable, parce que:"]
       [:ul {:id "92228ece-2f35-4447-b475-666e5a3ae874"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Votre système n’existe pas encore,"]]
       [:ul {:id "bbd18c7d-4106-4491-8fa4-34917dc5ade8"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Le test est risqué pour votre relation client, l’environnement, …"]]
       [:ul {:id "11dc508e-16c3-4ec6-b1a6-a693fa0a4f19"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Faire le changement est trop cher sans être sûr de son utilité,"]]
       [:ul {:id "c7dfa1d8-7127-498b-82ef-cde83ed3182f"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Le test est destructif (par exemple vous ne pouvez pas défaire le changement à évaluer)"]]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Simulation technique"]
      [:div {:class "indented"}
       [:p {:id "44f25347-e0c0-468c-be92-6c762853db97"
            :class ""}
        "La table ci-dessous montre différentes techniques de simulation, elle illustre des techniques complètement différentes pour évaluer une réponse potentielle à un problème sur votre système."]
       [:p {:id "708bc8c2-8e65-4f31-be49-9cde744daccc"
            :class ""}
        "Notez que toutes ne s’appliquent pas au contexte de la chaîne logistique, elles sont là pour illustrer ce qu’est une simulation."]
       [:figure {:class "block-color-gray_background callout"
                 :style {"whiteSpace" "pre-wrap"
                         "display" "flex"}
                 :id "90ade946-8951-4e7d-92de-060a80bde93c"}
        [:div {:style {"fontSize" "1.5em"}}
         [:span {:class "icon"}
          "💡"]]
        [:div {:style {"width" "100%"}}
         "La simulation est une technique ancienne, la plupart des exemples de techniques ci-dessous ont été utilisées de manière intensive depuis le début du vingtième siècle."]]
       [:table {:id "bdd43676-c6cf-4a08-aeb4-f1191cc85ae0"
                :class "simple-table"}
        [:thead {:class "simple-table-header"}
         [:tr {:id "90791e5e-f348-4bf9-b0f8-1beabd72a175"}
          [:th {:id "UznU"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "185px"}}
           "Des techniques de simulation"]
          [:th {:id "OScO"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "349px"}}
           "Description"]
          [:th {:id "<sa:"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "578px"}}
           "Exemples"]]]
        [:tbody
         [:tr {:id "048b2aec-6c1c-46b0-8bae-c2eda27c361b"}
          [:th {:id "UznU"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "185px"}}
           "Programmes informatiques"]
          [:td {:id "OScO"
                :class ""
                :style {"width" "349px"}}
           "Des logiciels de simulation: modélisation 3D, la simulation à événements discrets, la simulation par éléments finis."]
          [:td {:id "<sa:"
                :class ""
                :style {"width" "578px"}}
           "3D modeling: Concevoir les engrenages d’un moteur." [:br]
           "Simulation à événements discrets: Diminuer le niveau de stock de votre atelier"
           [:br]
           "Simulation par éléments finis: Déformation du bouclier d’un satellite artificiel durant son entrée dans l’atmosphère."
           [:br]]]
         [:tr {:id "1379d04c-7ab4-4f93-8c99-6b2bdb517d1d"}
          [:th {:id "UznU"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "185px"}}
           "Expérimentation dans  une soufflerie"]
          [:td {:id "OScO"
                :class ""
                :style {"width" "349px"}}
           "Construire un objet en argile"]
          [:td {:id "<sa:"
                :class ""
                :style {"width" "578px"}}
           "Utile pour comprendre la pénétration dans l’air d’un modèle de voiture."]]
         [:tr {:id "caa0416b-1a35-4216-ab2d-526be8aad940"}
          [:th {:id "UznU"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "185px"}}
           "Une maquette"]
          [:td {:id "OScO"
                :class ""
                :style {"width" "349px"}}
           "Une maquette à petite échelle du système"]
          [:td {:id "<sa:"
                :class ""
                :style {"width" "578px"}}
           "Une maquette d’un immeuble avant sa construction pour montrer le résultat attendu aux investisseurs"]]
         [:tr {:id "c29ec72d-5ee4-4573-b516-69f89f39047f"}
          [:th {:id "UznU"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "185px"}}
           "Prototype"]
          [:td {:id "OScO"
                :class ""
                :style {"width" "349px"}}
           "Un prototype - proche du résultat final attendu - mais fabriqué avec des ressources moins chères."]
          [:td {:id "<sa:"
                :class ""
                :style {"width" "578px"}}
           "Créer un prototype de voiture pour rencontrer les clients et voir leur retour pendant un salon de l’automobile."]]
         [:tr {:id "1a80752a-be00-483f-8eaf-46561818a824"}
          [:th {:id "UznU"
                :class "simple-table-header-color simple-table-header"
                :style {"width" "185px"}}
           "Calcul"]
          [:td {:id "OScO"
                :class ""
                :style {"width" "349px"}}
           "Calcul formel mathématique"]
          [:td {:id "<sa:"
                :class ""
                :style {"width" "578px"}}
           "Calcul de la trajectoire d’une fusée pour son envol vers la lune."]]]]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Simulation pour la chaîne logistique"]
      [:div {:class "indented"}
       [:p {:id "9e4bfca6-addf-405c-b4c9-db4779fe521f"
            :class ""}
        "La chaîne logistique peut profiter de la simulation pour aider la prise de décision. Notre offre de simulation se base sur la simulation à événements discrets - une technique utilisant des programmes informatiques, spécialement adaptée pour les questions que pose l’industrie."]
       [:p {:id "f6758ff7-82f9-4c1b-ae07-d31c4fab594d"
            :class ""}
        "La simulation à événements discrets permet de reproduire votre usine et son évolution dans le temps. Dans la publicité et la littérature, on utilise souvent le mot “jumeau digital”. C’est une façon de mettre en avant que vous pouvez faire des expérimentations sur un jumeau numérique plutôt que sur le système réel."]
       [:p {:id "54886c50-63cf-4332-b3a2-669045dc95a1"
            :class ""}
        "La simulation nécessite des outils pour reproduire l’évolution dans le temps, puisque les feuilles Excel traitent le plus souvent avec des données moyennes et des vues statiques dans le temps."]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.875em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Comment la simulation fonctionne?"]
      [:div {:class "indented"}
       [:p {:id "ad2ea648-d0b2-408f-a22f-9fe02487680a"
            :class ""}
        "La simulation utilise une approche scientifique, telle que décrite dans le diagramme ci-dessous:"]
       [:figure {:id "77baf5a6-746c-4088-8642-ba2a6df9b737"
                 :class "image"
                 :style {"textAlign" "center"}}
        [:a
         {:href
          "Qu%E2%80%99est-ce-que%20la%20simulation%206445c723624a4a169fb290fce0d54b75/Untitled.png"}
         [:img
          {:style {"width" "576px"}
           :src
           "Qu%E2%80%99est-ce-que%20la%20simulation%206445c723624a4a169fb290fce0d54b75/Untitled.png"}]]
        [:figcaption "L’approche scientifique appliquée à la simulation"]]
       [:ul {:id "928ac573-194a-4623-9991-6c14b687f250"
             :class "toggle"}
        [:li
         [:details {:open ""}
          [:summary "Description"]
          [:p {:id "5439fc74-c335-4d38-a178-eff524db0a93"
               :class ""}
           "Dans le texte ci-dessous, les"
           [:code "mots écrits ainsi"]
           "font référence aux boîtes contenant le même texte dans le diagramme."]
          [:p {:id "50e1ed77-94dd-496f-a394-7fdf359f3aa5"
               :class ""}
           "L’histoire commence avec:"]
          [:ul {:id "bed3c853-269a-4555-ab50-a2890c6079ee"
                :class "bulleted-list"}
           [:li {:style {"listStyleType" "disc"}}
            "le"
            [:code "Système réel"]]]
          [:ul {:id "124e9d1a-0e42-4c25-a1a0-21f1a3e0f75d"
                :class "bulleted-list"}
           [:li {:style {"listStyleType" "disc"}}
            "Une question de type"
            [:code "que se-passe-t-il si?"]
            "(e.g. Que se passe-t-il si le mix produit dans la demande client évolue)"]]
          [:p {:id "34b2ec62-713e-4f6e-bb78-c2ed2ba01740"
               :class ""}
           "Ensuite, nous faisons une analyse du système et créons un"
           [:code "Modèle"]
           "aussi simple que possible qui permettra de répondre à la question."
           [:code "Les données de votre système"]
           "façonnent le modèle que nous pouvons créer, puisque certaines informations peuvent ne pas être disponibles, ou pas suffisamment précises."]
          [:p {:id "f152b336-f1bc-4648-82e7-7862ce652f3e"
               :class ""}
           "Le"
           [:code "modèle"]
           "est alors"
           [:code "implémenté"]
           "dans un modèle de simulation, language dans lequel nous pouvons exécuter la simulation. C’est pendant cette exécution qu’est obtenue la trace de l’exécution et les valeurs des indicateurs de performance. Les"
           [:code "indicateurs"]
           "sont nécessaires pour évaluer la performance de la solution et sa vraisemblance."]
          [:p {:id "715969e9-7875-4b4d-a5fe-e6b0e8420618"
               :class ""}
           "Pour finir, la"
           [:code "valide"]
           "(validation) consiste à comparer le scénario et les effets attendus, ainsi que la vraisemblance. Pour obtenir cette validation, on compare souvent un scénario à un autre, ou on peut les montrer à des experts, ce qui accroît la confiance dans la solution."]
          [:p {:id "b6986ea3-773d-44a8-9371-e90bb7cb4ee8"
               :class ""}
           "Si le modèle utilisé n’est pas validé, il sera modifié en fonction, et la boucle complète démarre de nouveau."]
          [:p {:id "18573f46-2208-4c1a-93bc-9f0f178fe678"
               :class ""}
           "Si le modèle est validé, la solution testée est implémentée dans le monde réel."]]]]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.5em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "La simulation est pour les systèmes complexes"]
      [:div {:class "indented"}
       [:p {:id "40807699-2c68-4dd0-9248-8d29d2b6ea7b"
            :class ""}
        "Un"
        [:a {:href "https://fr.wikipedia.org/wiki/Syst%C3%A8me_complexe"}
         "système complexe"]
        "est fait de composants qui interagissent entre eux, et que leur comportement est intrinsèquement difficile à modéliser à cause de leur dépendances, la compétition entre eux, les relations entre eux ou d’autres types d’interactions entre les parties du système, ou pour finir entre le système et son environnement."]
       [:p {:id "76b9bceb-b8bd-441d-8480-be590cde87c8"
            :class ""}
        "Des exemples connus de systèmes complexes sont les colonies de fourmis, les systèmes écologiques, le climat, … ainsi que les usines."]
       [:p {:id "1d631e29-f4c7-4903-92db-76d79a6fb1b5"
            :class ""}
        "Dans votre quotidien, même si votre usine est bien définie et comprise, cela signifie que l’atelier peut devenir complexe à comprendre ou avoir des comportements inattendus. Il est probable que vous ayez ce sentiment de connaître votre usine, que vous avez des intuitions sur son fonctionnement, mais qu’elle aie quelques fois des comportements inattendus. Plus la situation est nouvelle et le plus cela risque d’arriver."]
       [:p {:id "125d7da1-5719-4dfc-b8de-1ebcd3902acc"
            :class ""}
        "Quand ces incertitudes surviennent, les conséquences pour votre usine sont:"]
       [:ul {:id "95d955f4-4e13-4f45-aed4-4f08ea5a1fba"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "vous ajoutez des retards dans les traitements,"]]
       [:ul {:id "d5e78b64-d7d9-4245-b6ef-5187a82e4cb1"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "vous créez du sur-stock,"]]
       [:ul {:id "b044f352-8913-413a-827d-611586cc5da4"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "ou vous acceptez que ce risque surviennent."]]
       [:p {:id "3d39dd3b-ecd9-4c28-9a01-2859aa62586c"
            :class ""}
        "Pour faire face à ces conséquences, la simulation permettra de transformer vos intuitions en faits. Avec des informations détaillées, il est assez facile d’aligner toutes les parties prenantes sur des faits démontrés dans la simulation. Votre prise de décision sera moins subjective et beaucoup plus basée sur des données."]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.5em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Quand utiliser la simulation à événements discrets?"]
      [:div {:class "indented"}
       [:p {:id "d858e420-51f3-48f5-9aa7-5f2d46fe591f"
            :class ""}
        "De manière générale, la simulation permet de répondre aux questions sur votre usine de type “Que se passe-t-il si?”, par exemple:"]
       [:div {:class "indented"}
        [:ul {:id "db583fff-808b-45e7-a732-9136d5edab5d"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Que se passe-t-il si le débit dans l’usine est augmenté?"]]
        [:ul {:id "e6a7c131-80f6-4f86-af55-fbcc40f7b9c2"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Que se passe-t-il si on autorise plus d’en cours?"]]
        [:ul {:id "0a1e3bab-89ed-4010-aee7-8f636036bc65"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Que se passe-t-il si la demande client voit son mix produit évoluer?"]]
        [:ul {:id "de4c5cb2-32db-4177-a736-18ecd858db4d"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Que se passe-t-il si ce nouveau produit est industrialisé dans le même atelier?"]]
        [:ul {:id "2523a743-e262-4b49-a7c7-cd95ce8df190"
              :class "bulleted-list"}
         [:li {:style {"listStyleType" "disc"}}
          "Que se passe-t-il si  nous ajoutons une nouvelle machine dans l’atelier?"]]]
       [:p]]]
     [:details {:open ""}
      [:summary {:style {"fontWeight" "600"
                         "fontSize" "1.5em"
                         "lineHeight" "1.3"
                         "margin" "0"}}
       "Les propriétés de la simulation à événements discrets"]
      [:div {:class "indented"}
       [:ul {:id "e6f97fb1-cd48-4541-aa9e-d693fbeb0beb"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Approche boîte blanche:"
         [:ul {:id "0f889614-b802-4b5c-a692-42fe54b1fc5d"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Tous les aspects de la simulation est explicable et vérifiable, dans le moindre détail."]]
         [:ul {:id "3ca39ba6-03be-4134-aa28-14577e44123c"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Cela rend la simulation adaptable (facile à modifier et à mettre à jour)."]]
         [:ul {:id "e1651994-9ba4-4fa1-915f-b6fb4a8afe38"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "La part utile du contenu de cette boîte blanche est la connaissance de vos experts"]]
         [:ul {:id "900505d3-9784-435b-bd07-3451f76f60bb"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "L’approche boîte blanche est l’opposé de l’approche boîte noire, dans laquelle seules les entrées et sorties sont compréhensibles, mais pas la manière par laquelle les sorties ont été obtenues."]]]]
       [:ul {:id "f781076d-8acb-490b-b19e-ccf927a24eda"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Progressif"
         [:ul {:id "c6e6203e-103f-4a22-bbf2-367feec386d0"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Nous commençons simple et complexifions au fur et à mesure."]]
         [:ul {:id "8be00237-9034-4387-a8ca-139cffcf03c7"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Nous commençons par un modèle simple que vous validez et ajouter ensuite de la complexité."]]
         [:ul {:id "1938a3a5-d262-48b7-98b9-78cfe347440d"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Cela aider à comprendre, expliquer et valider le modèle"]]]]
       [:ul {:id "2de7337b-e631-4e5c-be31-89af7ea897bc"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Dynamique"
         [:ul {:id "7df0f80a-d6e7-476c-bc83-6f127abf616d"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "La simulation modélise le temps et l’évolution du système dans le temps."]]]]
       [:ul {:id "ccf05511-b9ba-4b39-9693-39c32f615049"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Discret"
         [:ul {:id "170a0520-b0d6-4112-afff-306e8cff6b98"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Les changements se font à des moments précis dans le temps (par opposition à continu)."]]]]
       [:h3 {:id "05f6a087-12a5-4a71-8eb6-babf9834d8a8"
             :class ""}
        [:strong "Si la question le nécessite, la simulation peut aussi être:"]]
       [:ul {:id "e52360ee-ee9c-47b5-ab33-c9a95fb602a2"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Stochastique (probabilité)"
         [:ul {:id "b9568bb7-5d52-4ded-8c00-8272a885940b"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Des données ont été remplacées par des variables aléatoires sir le système physique est lui-même stochastique ou pour simplifier un système trop complexe à modéliser."]]
         [:ul {:id "17a02650-9d0a-4d6c-953a-5ee231b3af06"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Chaque scénario est évaluer plusieurs fois, et chaque évaluation remplace les variables aléatoires par un nombre tiré au hasard."]]
         [:ul {:id "b6e039f3-ecf7-402c-b025-8b3bd46756cd"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Les résultats sont souvent synthétisés par leur moyenne, leur écart type, le minimum et le maximum."]]]]
       [:ul {:id "be7ae48e-b5ff-441e-b819-5eff638d9bad"
             :class "bulleted-list"}
        [:li {:style {"listStyleType" "disc"}}
         "Fournir une évaluation de la robustesse"
         [:ul {:id "0ead6e2b-bb6c-4efd-a5fa-6f8fc43e1e7e"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "Grâce à la méthode de"
           [:a {:href
                "https://fr.wikipedia.org/wiki/M%C3%A9thode_de_Monte-Carlo"}
            "Monté Carlo"]
           ", la robustesse d’une solution peut être mesurée, grâce aux différents scénarios qui sont envisagés. Par exemple, une nouvelle politique de gestion peut être plus robuste que la politique en cours par rapport aux modifications du mix produit."]]
         [:ul {:id "695bd8b9-5579-4f84-8bb1-9b91679c8689"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "L’étude de robustesse est utile pour :"
           [:ul {:id "e8034bc0-7d87-412a-a94f-f6a700a5142f"
                 :class "bulleted-list"}
            [:li {:style {"listStyleType" "square"}}
             "vérifier si une politique est performante même si le mix produit évolue."]]
           [:ul {:id "b1f56988-8a79-4d16-9b4b-85e45962585a"
                 :class "bulleted-list"}
            [:li {:style {"listStyleType" "square"}}
             "si la planification est performante même si la durée des opérations n’est pas déterministe."]]]]
         [:ul {:id "2e51eaa6-eefe-47e8-bf3f-921d8174761e"
               :class "bulleted-list"}
          [:li {:style {"listStyleType" "circle"}}
           "La robustesse est le plus souvent étudiée avec une approche probabiliste."]]]]]]]]])

(defn notion-generated-simulation-pitch-en
  []
  [:article {:id "9ae261b8-8051-46c1-b99f-9277716f6da4"
             :class "page sans"}
   [:header
    [:h1 {:class "page-title"}
     "Two-minute pitch"]
    [:p {:class "page-description"}]]
   [:div {:class "page-body"}
    [:div
     [:p {:id "4bd9d4ca-77ae-4d77-ac16-ab8a5080f2fc"
          :class ""}
      "Welcome to Hephaistox, the supply chain craftsman!"]
     [:p {:id "48ff8ecf-44bf-487b-b25b-c2146f20d591"
          :class ""}
      "Are you an industrial? Do you have some difficult questions to answer? So think about working with Hephaistox. Mati and Anthony can help you if:"]
     [:ul {:id "0ecae9e6-84c7-4ae7-a589-34d402c78cde"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Do you have some doubts about the profitability of an investment?"]]
     [:ul {:id "fec6c28e-54ff-4058-bedb-6ffa658d7bf8"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Do you search for a different organization to decrease your energy consumption, but you have doubts about the relevance of your solutions?"]]
     [:ul {:id "ddaabef8-8712-4b59-9b7d-72eea3fa894e"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Do you search for alternatives to decrease the scrap?"]]
     [:ul {:id "14a5ded3-591e-4a5d-9f88-c3929b09c5d3"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Do you think you can decrease your stock level but you don’t know the detailed conditions?"]]
     [:ul {:id "0650857b-bd35-4ea7-9d9c-ae3a69bf9e48"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "And some other questions you may have…"]]
     [:p {:id "db16315f-0ac1-4654-a941-a62c755a3f14"
          :class ""}
      "While your experts and their expertise are invaluable, there are instances when the questions are too complex, the impacts extend beyond multiple domains, or the situation has never been encountered before. In such scenarios, we step in to assist both your experts and the decision-maker."]
     [:p {:id "06075df7-d43c-4263-a2a9-bd0521993c4c"
          :class ""}
      "By partnering with Hephaistox, you're not just getting consultants – you're gaining supply chain craftsmen dedicated to solving your unique challenges with custom-made solutions. The methodology we use is proven by our years of experience, it consists of the following main steps:"]
     [:ul {:id "b4e40711-1172-44fa-bc70-c92d429cb015"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Define the question"]
       ": With our Supply Chain knowledge and practical experience in simulation, we can help you to ask the right question. “A problem well-defined is a problem half-solved”"]]
     [:ul {:id "dcba343d-74ba-4367-a87b-9084cc9b2595"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Guide the modeling of your plant:"]
       "Our skills in modeling will help you to start with more important constraints."]]
     [:ul {:id "f1f79c66-d770-4945-bddb-c192b5c71a2d"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Solving the problem:"]
       "We come up with methods and toolings."]]
     [:ul {:id "f3963fb2-70b4-414d-a480-6b1b0322d21b"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Make the decision:"]
       "To create a convergence of all decision makers (whatever the domain is, from operators to managers)"]]
     [:p {:id "af696ab2-5416-4e86-9be6-9cca7187701a"
          :class ""}
      "That approach - using the scientific method - will give you confidence in your decision-making, and give precise decisions."]]
    [:p {:id "393f7e93-538b-4bc0-8936-02d121a10068"
         :class ""}
     "Schedule a meeting with us and let Hephaistox be the catalyst to take your supply chain to new level."]
    [:p {:id "7b9456f3-299e-402a-9425-24a11949bfdc"
         :class ""}]]])

(defn notion-generated-simulation-pitch-fr
  []
  [:article {:id "2e1e6980-504d-486c-a65f-37fe2bf9141e"
             :class "page sans"}
   [:header
    [:h1 {:class "page-title"}
     [:strong "Discours en deux minutes"]]
    [:p {:class "page-description"}]]
   [:div {:class "page-body"}
    [:p {:id "8d89593e-7446-46aa-8857-368f2a048d87"
         :class ""}
     "Bienvenue à Hephaistox, l’artisan de la chaîne logistique."]
    [:p {:id "7a5dcba0-8ccb-4e56-baed-19af52ec8816"
         :class ""}]
    [:div
     [:p {:id "4a504393-6216-4e51-a991-a2daf1ec73c7"
          :class ""}
      "Vous êtes industriels? Vous avez une décision difficile à prendre? et bien pensez à Hephaistox. Mati et Anthony (moi-même) pouvons vous aider si:"]
     [:ul {:id "c6b2d4ca-a527-40e9-a7d5-9d959f88ab8d"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Vous doutez de la rentabilité d’un nouvel investissement?"]]
     [:ul {:id "d5e61799-b4b0-43a0-9005-22632dc090f0"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Vous cherchez à vous organiser différemment pour diminuer votre consommation énergétique, mais vous avez des doutes sur la pertinence de vos solutions?"]]
     [:ul {:id "4d77ce7c-4ffb-4ae9-991b-0cc29187242c"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Vous cherchez des alternatives pour minimiser votre perte matière?"]]
     [:ul {:id "84bcdcef-086d-43b9-9181-4321f39d4a51"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Vous pensez pouvoir diminuer votre stock mais ne savez pas dans quelles conditions?"]]
     [:ul {:id "8f1f9399-257b-4acd-b204-5874df317819"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Vous industrialisez un nouveau produit et cherchez à anticiper son impact sur l’atelier existant?"]]
     [:ul {:id "0e58d93e-3e9a-4abd-a4a6-55bef6de6a2b"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       "Ainsi que des questions qui vous tiennent à coeur…"]]
     [:p {:id "afd1b794-beab-4d14-9c13-e754f17351fb"
          :class ""}
      "Vous avez des experts et leurs connaissances est irremplaçable, mais quand la question est complexe, que ses impacts font intervenir plusieurs domaines d’expertise, quand la situation est nouvelle, nous pouvons aider vos experts et ceux qui doivent prendre la décision."]
     [:p {:id "804bdfd7-98a0-4098-bdf0-8be4588048e4"
          :class ""}
      "Hephaistox est votre partenaire, au-delà d’une simple prestation de consultants - nous sommes des artisans de la chaîne logistique, fabriquant des solutions sur mesure, avec du logiciel de qualité. Nous utilisons une méthodologie qui a été éprouvée par nos années d’expériences. Elle consiste dans les étapes suivantes:"]
     [:ul {:id "9fd241ec-2e82-4fd8-9a63-6e65c67db632"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Définir le problème:"]
       "Grâce à notre connaissance de la chaîne logistique et des expériences industrielles variées, nous pouvons vous aider à bien poser votre question - un problème bien posé est à moitié résolu,"]]
     [:ul {:id "4ad96be4-90b1-4150-8dd2-5a258a6ae565"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Guider votre modélisation:"]
       "Nos compétences en modélisation vont vous guider à commencer par les choses les plus importantes d’abord,"]]
     [:ul {:id "685b8c5d-0ac3-4826-9f68-02975872db52"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Résolution:"]
       "Nous mettons en oeuvre nos méthodes et outils dans le cadre convenu ensemble."]]
     [:ul {:id "e188352e-e1bd-43d2-86e8-5f4d27f557df"
           :class "bulleted-list"}
      [:li {:style {"listStyleType" "disc"}}
       [:strong "Faire prendre la décision:"]
       "Et faire converger tous les acteurs sur une décision (quel que soit le métier / des opérateurs aux décideurs)."]]
     [:p {:id "63b2e6cb-ca10-4110-9ce7-696e181a0813"
          :class ""}
      "Cette approche est l’essence de l’approche scientifique, elle vous donnera confiance dans votre prise de décision, avec des éléments précis."]
     [:p {:id "208b927f-886a-4a61-8740-82b4dd9ccef2"
          :class ""}
      "Planifiez un rendez-vous avec nous et laissez Hephaistox être le catalyseur pour amener la chaîne logistique au prochain niveau."]]]])
