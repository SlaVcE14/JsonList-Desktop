module com.sj14apps.jsonlist.core {
    requires com.google.gson;
    requires okhttp3;
    requires org.jetbrains.annotations;
    exports com.sj14apps.jsonlist.core.controllers;
    exports com.sj14apps.jsonlist.core;
    requires kotlin.stdlib;
}