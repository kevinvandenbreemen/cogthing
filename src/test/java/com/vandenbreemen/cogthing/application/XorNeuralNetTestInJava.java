package com.vandenbreemen.cogthing.application;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.jupiter.api.Test;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class XorNeuralNetTestInJava {

    @Test
    public void shouldSetUpNeuralNetForXOR() {

        int nEpochs = 10000;    // number of training epochs

        // list off input values, 4 training samples with data for 2
        // input-neurons each
        INDArray input = Nd4j.zeros(4, 2);

        // correspondending list with expected output values, 4 training samples
        // with data for 2 output-neurons each
        INDArray labels = Nd4j.zeros(4, 2);

        // create first dataset
        // when first input=0 and second input=0
        input.putScalar(new int[]{0, 0}, 0);
        input.putScalar(new int[]{0, 1}, 0);
        // then the first output fires for false, and the second is 0 (see class comment)
        labels.putScalar(new int[]{0, 0}, 1);
        labels.putScalar(new int[]{0, 1}, 0);

        // when first input=1 and second input=0
        input.putScalar(new int[]{1, 0}, 1);
        input.putScalar(new int[]{1, 1}, 0);
        // then xor is true, therefore the second output neuron fires
        labels.putScalar(new int[]{1, 0}, 0);
        labels.putScalar(new int[]{1, 1}, 1);

        // same as above
        input.putScalar(new int[]{2, 0}, 0);
        input.putScalar(new int[]{2, 1}, 1);
        labels.putScalar(new int[]{2, 0}, 0);
        labels.putScalar(new int[]{2, 1}, 1);

        // when both inputs fire, xor is false again - the first output should fire
        input.putScalar(new int[]{3, 0}, 1);
        input.putScalar(new int[]{3, 1}, 1);
        labels.putScalar(new int[]{3, 0}, 1);
        labels.putScalar(new int[]{3, 1}, 0);

        DataSet ds = new org.nd4j.linalg.dataset.DataSet(input, labels);

        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder().weightInit(WeightInit.XAVIER)
                .activation(Activation.RELU)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Sgd(0.05))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(2).nOut(3).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nOut(2)
                        .activation(Activation.SOFTMAX)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(config);
        net.init();

        net.setListeners(new ScoreIterationListener(100));

        // C&P from LSTMCharModellingExample
        // Print the number of parameters in the network (and for each layer)
        System.out.println(net.summary());

        // here the actual learning takes place
        for( int i=0; i < nEpochs; i++ ) {
            net.fit(ds);
        }

        // create output for every training sample
        INDArray output = net.output(ds.getFeatures());
        System.out.println(output);

        // let Evaluation prints stats how often the right output had the highest value
        Evaluation eval = new Evaluation();
        eval.eval(ds.getLabels(), output);
        System.out.println(eval.stats());

    }

}
