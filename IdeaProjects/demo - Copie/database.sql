-- Création de la table itineraire
CREATE TABLE `itineraire` (
  `idItin` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifiant de l''itinéraire',
  `villeDepart` varchar(100) NOT NULL COMMENT 'Ville de départ',
  `villeArrivee` varchar(100) NOT NULL COMMENT 'Ville d''arrivée',
  `arrets` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Liste des arrêts' CHECK (json_valid(`arrets`)),
  `Distance` float NOT NULL COMMENT 'Distance totale en kilomètres',
  `dureeEstimee` time NOT NULL COMMENT 'Durée estimée du trajet',
  PRIMARY KEY (`idItin`),
  INDEX `idx_villeDepart` (`villeDepart`),
  INDEX `idx_villeArrivee` (`villeArrivee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Création de la table bus
CREATE TABLE `bus` (
  `idBus` int(11) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du bus',
  `modele` varchar(100) NOT NULL COMMENT 'modele du bus',
  `capacite` int(11) NOT NULL COMMENT 'nombre de places',
  `plaqueImmat` varchar(50) NOT NULL COMMENT 'Immatriculation',
  `idItineraire` int(11) NOT NULL COMMENT 'Clé étrangère vers Itineraire',
  `type` char(100) NOT NULL COMMENT 'Type du bus',
  `Tarif` double NOT NULL COMMENT 'Tarif d''un trajet ou par location',
  PRIMARY KEY (`idBus`),
  INDEX `idx_idItineraire` (`idItineraire`),
  INDEX `idx_plaqueImmat` (`plaqueImmat`),
  CONSTRAINT `fk_bus_itineraire` FOREIGN KEY (`idItineraire`) REFERENCES `itineraire` (`idItin`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 