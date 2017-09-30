package com.nohana.http;


import java.util.Collection;

@FunctionalInterface
public interface KeyMaterialFactory {

    Collection<KeyMaterial> get();

}
