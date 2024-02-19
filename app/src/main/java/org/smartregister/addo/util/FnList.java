package org.smartregister.addo.util;

import androidx.annotation.NonNull;

import org.smartregister.addo.util.FnInterfaces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class FnList provides functional operations on a list of items.
 * <p>
 * WARNING: This class is designed to swallow both NULLS and Exceptions occurring during its operations.
 * Any exceptions thrown during operations such as map, filter, etc. will be caught
 * and will not propagate to the calling code. Instead, all errors are logged using Timber.e.
 * <p>
 * This behavior ensures smooth operation of the pipeline but can potentially hide issues
 * or unexpected behaviors. When debugging or troubleshooting, be sure to check the logs
 * for any relevant error messages from this class.
 * <p>
 * It's important to be aware of this when using this class, especially if
 * operations have side effects or if there's a need to know about any failures.
 * <p><strong>This class can only be iterated once</strong><p/>
 */
@SuppressWarnings({"unchecked","unused"})
public class FnList<T> implements Iterable<T>{

    private final LazyIterator<T> lazy;

    private FnList(LazyIterator<T> lazyIterator){
        lazy = lazyIterator.copy();
    }

    @NonNull public static <T>FnList<T> from(Collection<T> collection){
        Iterator<T> it=collection.iterator();
        return FnList.generate(()->{
            while(it.hasNext()){
                T t= it.next();
                if(t!=null) return t;
            }
            return null;
        });
    }

    @NonNull public static <T>FnList<T> from(T[] array){
        return from(Arrays.asList(array));
    }

    @NonNull public static FnList<String> from(String source, Pattern getMatching){
            Matcher m=getMatching.matcher(source);
            return generate(()->m.find()?m.group():null);
    }
    @NonNull public static <T> FnList<T> generate(Producer<T>generator){
        return new FnList<>(new LazyIterator<>(generator));
    }
    @NonNull public static <T> FnList<T> generate(Function<Integer,T>generator){
        Mutable<Integer> mutable=new Mutable<>(0);
        return generate(()-> generator.apply(mutable.value++));
    }
    @NonNull public static FnList<Integer> range(int lower, int upper){
        return generate(x->lower+x<upper?lower+x:null);
    }

    @NonNull public static FnList<Integer> range(int upper){
        return generate(x->x<upper?x:null);
    }

    @NonNull public <S> FnList<S> map(BiFunction<T,Integer, S> function) {
        lazy.addOperation((input,index) -> (T) function.apply(input,index));
        // Cast is safe here because we're just transforming data.
        return (FnList<S>) new FnList<>(lazy);
    }

    @NonNull public FnList<T> filter(BiPredicate<T,Integer> predicate) {
        return map((t,i)->predicate.test(t,i)?t:null);
    }
    @NonNull public FnList<T> filter(Predicate<T> predicate) {
        return map(t->predicate.test(t)?t:null);
    }
    @NonNull public <S> FnList<S> map(Function<T, S> function) {
        return map((t,i)->function.apply(t));
    }


    @NonNull public <S> FnList<S> flatMap(Function<T, Collection<S>> function) {
       map(function);
       return (FnList<S>) flat();
    }

    @NonNull public <S> FnList<S> flatMapArrays(Function<T, S[]> function) {
        map(function).map(it->Arrays.asList(it));
        return (FnList<S>) flat();
    }

    @NonNull public  FnList<?> flat() {
        LazyIterator<T> lazyCopy=lazy.copy();
        Mutable<Iterator<?>> mutable=new Mutable<>(null);
        return FnList.generate(i->{
            while(mutable.value == null || !mutable.value.hasNext()){
                T nextItem=lazyCopy.next();
                if(nextItem instanceof Collection){
                    mutable.value=((Collection<?>)nextItem).iterator();
                }
                else if (nextItem instanceof Object[]){
                    mutable.value=(Arrays.asList((T[])nextItem)).iterator();
                }
                else{ return nextItem;}
            }
            return mutable.value.next();
        });
    }

    @NonNull public <R> R reduce(@NonNull  R identity, TriFunction<R,T,Integer,R> accumulator) {
        Mutable<R> k=new Mutable<>(identity);
        forEachItem((item,index)-> k.value = accumulator.apply(k.value,item,index));
        return k.value;
    }

    @NonNull public <R> R reduce(R identity, BiFunction<R,T,R> function) {
        return reduce(identity,(a,b,index)-> function.apply(a,b));
    }

    @NonNull public FnList<T> unique(){ return unique(x->x); }

    @NonNull public <S> FnList<T> unique(Function<T, S> giveId){
        Set<S> ids = new LinkedHashSet<>();ids.add(null);
        return filter(x->ids.add(Fn.ex(()->giveId.apply(x))));
    }

    @NonNull public <S> Map<S, List<T>> group(Function<T,S> giveId){
        return group((t,i)->giveId.apply(t));
    }
    @NonNull public <S> Map<S, List<T>> group(BiFunction<T,Integer, S> giveId){
        Map<S,List<T>> map = new HashMap<>();
        return reduce(map,(m,t,index)->{
            S id=giveId.apply(t,index);
            List<T> list = m.computeIfAbsent(id, k -> new ArrayList<>());
            list.add(t);
            return m;
        });
    }

    @NonNull public <S>FnList<S> lookup(Map<T,S> dictionary){
        return map(dictionary::get);
    }

    @NonNull public <S> Map<S,T> zip(List<S> keys){
       return reduce(new HashMap<>(),(m,item,index)->{
           m.put(keys.get(index),item);
           return m;
       });
    }

    @NonNull public <S> Map<S,T> zip(S[] keys){return zip(Arrays.asList(keys));}
    @NonNull public List<T> list(){
        return reduce(new ArrayList<>(),(a,b)->{a.add(b);return a;});
    }

    @NonNull public Set<T> toSet(){
        return new LinkedHashSet<>(unique().list());
    }

    @NonNull public String toString(){
        return list().toString();
    }

    @NonNull public String toJson(){
        return  Fn.getGSON().toJson(list());
    }

     public void forEachItem(Consumer<T> action) {
        for(T t:this){ Fn.ex(()->action.take(t)); }
    }

    public void forEachItem(BiConsumer<T,Integer> consumer) {
        Mutable<Integer> mutable=new Mutable<>(0);
        for(T t:this){ Fn.ex(()->consumer.take(t,mutable.value++)); }
    }

    @NonNull @Override public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
    @NonNull public FnList<T> prune() {
        return filter(it -> it != null
                && (!(it instanceof String) || !((String) it).trim().isEmpty())
                && (!(it instanceof Collection) || !((Collection<?>) it).isEmpty())
                && (!(it instanceof Map) || !((Map<?, ?>) it).isEmpty())
                && (!(it instanceof CharSequence) || ((CharSequence) it).length() > 0)
                && ( !it.getClass().isArray() || java.lang.reflect.Array.getLength(it) > 0 )
        );
    }
    @NonNull @Override
    public Iterator<T> iterator() { return lazy;}

}