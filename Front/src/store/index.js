import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

import music from './music';

export const store = new Vuex.Store({
    modules: {
        music,
    },
    strict: process.env.NODE_ENV !== 'production'
});
