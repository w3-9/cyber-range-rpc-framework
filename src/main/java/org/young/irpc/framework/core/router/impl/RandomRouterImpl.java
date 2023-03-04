package org.young.irpc.framework.core.router.impl;

import org.young.irpc.framework.core.common.channel.ChannelFuturePoolingRef;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.router.IRouter;
import org.young.irpc.framework.core.router.Selector;

import java.util.*;


/**
 * @ClassName RandomRouterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/2/21 下午9:54
 * @Version 1.0
 **/
public class RandomRouterImpl implements IRouter {
    @Override
    public void refreshRouterArray(Selector selector) {
        List<ChannelFutureWrapper> wrapperList = CommonClientCache.CONNECT_MAP.getOrDefault(selector.getProviderServiceName(),
                new ArrayList<>());
        int size = wrapperList.size();
        ChannelFutureWrapper[] wrappers = new ChannelFutureWrapper[size];
        int[] randomResult = createRandomIndex(size);
        for (int i = 0; i < size; i++){
            wrappers[i] = wrapperList.get(randomResult[i]);
        }
        CommonClientCache.SERVER_ROUTER_MAP.put(selector.getProviderServiceName(),wrappers);

        URL url = new URL();
        url.setServiceName(selector.getProviderServiceName());
        CommonClientCache.ROUTER.updateWeight(url);
    }

    /**
     * Len should not exceed 256.
     * @param len
     * @return
     */
    private int[] createRandomIndex(int len){
        int[] arr = new int[len];
        Random random = new Random();
        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < len; i++){
            int tmp = random.nextInt(len);
            while (set.contains(tmp)){
                tmp = random.nextInt(len);
            }
            arr[i] = tmp;
        }
        return arr;
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CommonClientCache.REF.get(selector);
    }

    @Override
    public void updateWeight(URL url) {
        List<ChannelFutureWrapper>  channelFutureWrappers
                = CommonClientCache.CONNECT_MAP
                .getOrDefault(url.getServiceName(),new ArrayList<>());
        Integer[] weights = createWeightArr(channelFutureWrappers);
        createRandomArr(weights);
        ChannelFutureWrapper[] randomWrappers =
                new ChannelFutureWrapper[weights.length];
        for (int i = 0; i < weights.length; i++){
            randomWrappers[i] = channelFutureWrappers.get(weights[i]);
        }
        CommonClientCache.SERVER_ROUTER_MAP
                .put(url.getServiceName(),randomWrappers);
    }

    private static Integer[] createWeightArr(List<ChannelFutureWrapper> wrappers){
        List<Integer> weights = new ArrayList<>();
        for (int i = 0; i < wrappers.size(); i++){
            int weight = wrappers.get(i).getWeight();
            int c = weight/100;
            for (int j = 0; j<c; j++){
                weights.add(i);
            }
        }
        Integer[] weightArr = new Integer[weights.size()];
        return weights.toArray(weightArr);
    }

    private static void createRandomArr(Integer[] arr){
        int size = arr.length;
        Random random = new Random();
        for (int i = 0; i<size; i++){
            int swapInd = random.nextInt(size);
            int tmp = arr[i];
            arr[i] = arr[swapInd];
            arr[swapInd] = tmp;
        }
    }

    public static void main(String[] args) {
        List<ChannelFutureWrapper> channelFutureWrappers = new ArrayList<>();
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 100));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 200));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 9300));
        channelFutureWrappers.add(new ChannelFutureWrapper(null, null, 400));
        Integer[] r = createWeightArr(channelFutureWrappers);
    }
}
