package data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by OROMAR on 08/07/2015.
 */
public interface DBHelper<T extends BasicEntity> {
    void create(T t);
    void delete(Serializable id);
    void edit(T t);
    T get(Serializable id);
    List<T> list();
    List<T> search(T t) throws IllegalAccessException;

}
