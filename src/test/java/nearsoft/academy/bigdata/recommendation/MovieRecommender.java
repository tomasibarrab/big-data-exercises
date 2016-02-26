package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.BiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomas on 25/02/16.
 */
public class MovieRecommender{

    public AdaptedDataModel model;
    private int totalLines;
    private BiMap<String, Integer> totalUsers;
    private BiMap<String, Integer> totalProducts;
    private UserBasedRecommender recommender;

    public MovieRecommender(String pathToFile) throws IOException,TasteException {

        model = new AdaptedDataModel(new File(pathToFile));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        totalLines = model.getCount();
        totalUsers = model.getUsers();
        totalProducts = model.getProducts();

    }

    public int getTotalReviews() {
        return totalLines;
    }

    public int getTotalProducts() throws TasteException {
        return model.getNumItems();
    }

    public int getTotalUsers() throws TasteException {
        return model.getNumUsers();
    }


    public List<String> getRecommendationsForUser(String userId) throws TasteException {
        List<RecommendedItem> recommendations = null;
        recommendations = recommender.recommend(totalUsers.get(userId), 3);
        List<String> recomendationsForUser = new ArrayList();

        for (RecommendedItem item : recommendations) {
            recomendationsForUser.add(totalProducts.inverse().get((int)item.getItemID()));
        }
        return recomendationsForUser;
    }

}
