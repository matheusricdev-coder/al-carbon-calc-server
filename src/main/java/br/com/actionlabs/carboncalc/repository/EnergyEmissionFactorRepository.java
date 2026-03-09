package br.com.actionlabs.carboncalc.repository;


import br.com.actionlabs.carboncalc.model.EnergyEmissionFactor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyEmissionFactorRepository extends MongoRepository<EnergyEmissionFactor, String> {

}
