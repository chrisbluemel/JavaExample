package NLP;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotatorDocker;
import org.hucompute.textimager.uima.gervader.GerVaderSentiment;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class NLPAnnotator{

    private AnalysisEngine pipeline;

    /**
     * Constructor for the NLPAnnotator class that sets up the configuration for the NLP Pipeline.
     * @throws ResourceInitializationException Gets thrown if the ressource initialization failed.
     * @author Guiseppe Abrami, Christian Bluemel
     */
    public NLPAnnotator() throws ResourceInitializationException {

        AggregateBuilder builder = new AggregateBuilder();
        try {
            builder.add(createEngineDescription(SpaCyMultiTagger3.class,
                    SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://spacy.lehre.texttechnologylab.org"
            ));

            builder.add(createEngineDescription(GerVaderSentiment.class,
                    GerVaderSentiment.PARAM_REST_ENDPOINT, "http://gervader.lehre.texttechnologylab.org",
                    GerVaderSentiment.PARAM_SELECTION, "text ,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
            ));

            builder.add(createEngineDescription(LabelAnnotatorDocker.class,
                    LabelAnnotatorDocker.PARAM_FASTTEXT_K, 100,
                    LabelAnnotatorDocker.PARAM_CUTOFF, false,
                    LabelAnnotatorDocker.PARAM_SELECTION, "text",
                    LabelAnnotatorDocker.PARAM_TAGS, "ddc3",
                    LabelAnnotatorDocker.PARAM_USE_LEMMA, true,
                    LabelAnnotatorDocker.PARAM_ADD_POS, true,
                    LabelAnnotatorDocker.PARAM_POSMAP_LOCATION, "am_posmap.txt",
                    LabelAnnotatorDocker.PARAM_REMOVE_FUNCTIONWORDS, true,
                    LabelAnnotatorDocker.PARAM_REMOVE_PUNCT, true,
                    LabelAnnotatorDocker.PARAM_REST_ENDPOINT, "http://ddc.lehre.texttechnologylab.org"
            ));

            pipeline = builder.createAggregate();

        } catch(ResourceInitializationException e){
            e.printStackTrace();
            System.out.println("Failed collecting the annotators");
        }
    }

    /**
     * Runs the annotation on a single JCas object using the already set up pipeline.
     * @param jcas the JCas object to be processed.
     * @throws AnalysisEngineProcessException Throws if there is an exception while analyzing the JCas file.
     */
    public void processJCas(JCas jcas) throws AnalysisEngineProcessException {
        try{
            SimplePipeline.runPipeline(jcas, pipeline);
        } catch(AnalysisEngineProcessException e){
            e.printStackTrace();
            System.out.println("Failed analyzation for" + jcas.toString());
        }
    }
}
