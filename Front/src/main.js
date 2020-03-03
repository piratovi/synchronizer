import Vue from 'vue';
import App from './App.vue';
import router from './router/router';
import {store} from './store/index';

store.dispatch('music/itemsLoad').then(function(){
  store.dispatch('music/extLoad');
  new Vue({
    router,
    store,
    render: h => h(App)
  }).$mount('#app')
});

Vue.config.productionTip = false;

