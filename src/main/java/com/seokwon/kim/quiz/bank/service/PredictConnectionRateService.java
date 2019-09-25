package com.seokwon.kim.quiz.bank.service;

import com.seokwon.kim.quiz.bank.exception.NotFoundDeviceException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class PredictConnectionRateService {
    private final MongoTemplate mongoTemplate;

    private final static String CONNECTION_DOC_NAME = "CONNECTION";
    private final static double THIS_YEAR = 2019;

    @Autowired
    public PredictConnectionRateService(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Double predictRateByDevice(final String deviceId) {
        AggregationResults<Document> doc = mongoTemplate.aggregate(
                newAggregation(
                        match(Criteria.where("device_id").is(deviceId)),
                        sort(Sort.by("year").ascending()),
                        project("year", "rate")
                ),
                CONNECTION_DOC_NAME,
                Document.class
        );
        return calcPredict(doc.getMappedResults());
    }

    private static final Double calcPredict(final List<Document> documents) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        if ( documents.isEmpty() ) {
            throw new NotFoundDeviceException();
        }
        for ( Document doc : documents ) {
            obs.add(doc.getInteger("year"), doc.getDouble("rate"));
        }
        PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(2).withMaxIterations(100);

        PolynomialFunction polynomialFunction = new PolynomialFunction(polynomialCurveFitter.fit(obs.toList()));
        return polynomialFunction.value(THIS_YEAR);
    }
}
