package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.common.iterator.FileLineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by Ariel Isaac Machado on 21/08/15.
 */
public class AdaptedDataModel extends FileDataModel {
    private BiMap<String, Integer> users;
    private BiMap<String, Integer> products;
    private File dataFile;
    private int count;


    public AdaptedDataModel(File DataFile) throws IOException {
        super(DataFile, false, 60 * 1000L, " ");
    }

    @Override
    protected void processFileWithoutID(FileLineIterator dataOrUpdateFileIterator, FastByIDMap<FastIDSet> data, FastByIDMap<FastByIDMap<Long>> timestamps) {
        processFile(dataOrUpdateFileIterator, data, timestamps, true);
    }

    public BiMap<String, Integer> getUsers() {
        return users;
    }

    public BiMap<String, Integer> getProducts() {
        return products;
    }

    public int getCount() {
        return count;
    }

    @Override
    protected void processFile(FileLineIterator dataOrUpdateFileIterator, FastByIDMap<?> data,
                               FastByIDMap<FastByIDMap<Long>> timestamps, boolean fromPriorData) {
        users = HashBiMap.create();
        products = HashBiMap.create();
        count = 0;
        String aux[] = {"", ""};
        StringBuilder lineToProcess = new StringBuilder("");

        while (dataOrUpdateFileIterator.hasNext()) {
            String line = dataOrUpdateFileIterator.next();
            if (line.startsWith("product/productId:")) {
                aux[1] = line.substring(line.lastIndexOf(' ') + 1);
                if (!products.containsKey(aux[1])) {
                    products.put(aux[1], products.size());
                }
            } else if (line.contains("review/userId:")) {

                aux[0] = line.substring(line.lastIndexOf(' ') + 1);
                if (!users.containsKey(aux[0])) {
                    users.put(aux[0], users.size() + 1);
                }
            } else if (line.startsWith("review/score:")) {
                lineToProcess.append(users.get(aux[0]));
                lineToProcess.append(" ");
                lineToProcess.append(products.get(aux[1]));
                lineToProcess.append(" ");
                lineToProcess.append(line.substring(line.lastIndexOf(' ') + 1));

                processLine(lineToProcess.toString(), data, timestamps, fromPriorData);
                lineToProcess.setLength(0);
                count++;
            }
        }

    }

    @Override
    protected DataModel buildModel() throws IOException {

        dataFile = super.getDataFile();
        FastByIDMap<FastByIDMap<Long>> timestamps = new FastByIDMap<FastByIDMap<Long>>();
        FastByIDMap<Collection<Preference>> data = new FastByIDMap<Collection<Preference>>();
        processFile(new FileLineIterator(dataFile, false), data, timestamps, false);

        return new GenericDataModel(GenericDataModel.toDataMap(data, true), timestamps);
    }

}
