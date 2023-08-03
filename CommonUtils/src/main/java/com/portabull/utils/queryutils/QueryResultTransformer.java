package com.portabull.utils.queryutils;

import com.portabull.constants.PortableConstants;
import com.portabull.payloads.MISPayload;
import com.portabull.response.MISTextResponse;
import com.portabull.utils.validationutils.Validations;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class QueryResultTransformer {

    private QueryResultTransformer() {
    }


    /**
     * It will form data as json object key value pair
     *
     * @param query
     * @return
     */

    public static List<Map<String, Object>> prepareResultTransformer(Query query) {
        query.setResultTransformer(
                new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        Map<String, Object> result = new LinkedHashMap<>(tuple.length);
                        for (int i = 0; i < tuple.length; i++) {
                            String alias = aliases[i];
                            if (alias != null) {
                                result.put(alias, tuple[i]);
                            }
                        }
                        return result;
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                }
        );
        return query.list();
    }

    /**
     * It will form data as json object key value pair
     *
     * @param query
     * @return
     */

    public static List<Map<String, Object>> prepareResultTransformer(Query query, Map<String, String> aliasNames) {
        query.setResultTransformer(
                new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        Map<String, Object> result = new LinkedHashMap<>(tuple.length);
                        for (int i = 0; i < tuple.length; i++) {
                            String alias = aliasNames.containsKey(aliases[i]) ? aliasNames.get(aliases[i]) : aliases[i];
                            if (alias != null) {
                                result.put(alias, tuple[i]);
                            }
                        }
                        return result;
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                }
        );
        return query.list();
    }

    /**
     * It will form data as json object key value pair and here preparing custom response to generate text file
     *
     * @param query
     * @return
     */

    public static MISTextResponse prepareCustomResult(Query query, Map<String, String> aliasNames) {
        MISTextResponse response = new MISTextResponse();
        Map<String, Integer> columnLength = new LinkedHashMap<>();
        query.setResultTransformer(
                new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        Map<String, Object> result = new LinkedHashMap<>(tuple.length);
                        for (int i = 0; i < tuple.length; i++) {
                            String alias = aliasNames.containsKey(aliases[i]) ? aliasNames.get(aliases[i]) : aliases[i];
                            if (alias != null) {
                                prepareColumnLength(columnLength, alias, tuple[i]);
                                result.put(alias, tuple[i]);
                            }
                        }
                        return result;
                    }

                    private void prepareColumnLength(Map<String, Integer> columnLength, String alias, Object data) {

                        if (columnLength.get(alias) == null) {
                            columnLength.put(alias, alias.length());

                            if (data != null && data.toString().length() > alias.length())
                                columnLength.put(alias, data.toString().length());

                            return;
                        }

                        if (data != null && data.toString().length() > columnLength.get(alias)) {
                                columnLength.put(alias, data.toString().length());
                        }
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                }
        );
        response.setColumnLength(columnLength);
        response.setQueryResult(query.list());
        return response;
    }


    /**
     * It will customize the data based on key values list
     *
     * @param query
     * @return
     */

    public static Map<String, List<Object>> prepareResultAsKeyValueList(Query query) {
        Map<String, List<Object>> data = new LinkedHashMap<>();
        query.setResultTransformer(
                new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] keys) {

                        for (int i = 0; i < tuple.length; i++) {
                            String alias = keys[i];
                            if (alias != null) {
                                if (!data.containsKey(alias)) data.put(alias, asList(tuple[i]));
                                else data.get(alias).add(tuple[i]);
                            }
                        }
                        return null;
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                }
        );
        query.list();
        return data;
    }

    /**
     * It seperates a structured data based on the requirements
     *
     * @param query
     * @return
     */

    public static Map<String, Map<String, List<Object>>> prepareResultAsKeyValueSeperateList(Query query) {
        Map<String, Map<String, List<Object>>> data = new LinkedHashMap<>();
        AtomicReference<Integer> sequence = new AtomicReference<>(1);

        query.setResultTransformer(
                new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] keys) {
                        int count = 0;
                        for (int i = 0; i < tuple.length; i++) {
                            String alias = keys[i];
                            if (alias != null) {

                                if (CollectionUtils.isEmpty(data)) {
                                    Map<String, List<Object>> map = new LinkedHashMap<>();
                                    map.put("1", asList(alias));
                                    data.put(PortableConstants.KEYS, map);
                                    count++;
                                } else {
                                    if (count != 0) {
                                        data.get(PortableConstants.KEYS).get("1").add(alias);
                                    }
                                }

                                if (count == 0) {
                                    if (data.containsKey(PortableConstants.VALUES)) {
                                        if (data.get(PortableConstants.VALUES).containsKey(sequence.get().toString())) {
                                            data.get(PortableConstants.VALUES).get(sequence.get().toString()).add(tuple[i]);
                                        } else {
                                            data.get(PortableConstants.VALUES).put(sequence.get().toString(), asList(tuple[i]));
                                        }
                                    } else {
                                        Map<String, List<Object>> map = new LinkedHashMap<>();
                                        map.put(sequence.get().toString(), asList(tuple[i]));
                                        data.put(PortableConstants.VALUES, map);
                                    }

                                }

                                if (i == tuple.length - 1) {
                                    sequence.getAndSet(sequence.get() + 1);
                                }

                            }
                        }
                        return null;
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                }
        );
        query.list();
        return data;
    }

    /**
     * Just changing the structure of the data
     *
     * @param data
     * @return
     */

    public static Map<String, Object[]> structureData(Map<String, Map<String, List<Object>>> data) {
        if (CollectionUtils.isEmpty(data))
            return null;

        Map<String, Object[]> map = new LinkedHashMap<>();

        if (data.containsKey(PortableConstants.KEYS)) {
            Map<String, List<Object>> keys = data.get(PortableConstants.KEYS);
            keys.forEach((key, value) ->
                    map.put(key, value.toArray())
            );

        }
        if (data.containsKey(PortableConstants.VALUES)) {
            Map<String, List<Object>> values = data.get(PortableConstants.VALUES);
            values.forEach((key, value) ->
                    map.put(key, value.toArray())
            );
        }
        return map;

    }

    /**
     * just overridden the asList method in ArrayList
     *
     * @param var1
     * @return
     */

    public static <T> List<T> asList(Object... var1) {
        return new ArrayList(Arrays.asList(var1));
    }


    public static Map<String, String> prepareAliasNames(MISPayload payload) {
        Map<String, String> aliasNames = new LinkedHashMap<>();
        payload.getSelectClause().forEach(selectClause -> {
            if (!Validations.isStringEmpty(selectClause.getAliasName())) {
                aliasNames.put(selectClause.getSelectName(), selectClause.getAliasName());
            }
        });
        return aliasNames;
    }
}
