import Vue from 'vue'
import VueRouter from 'vue-router'

import About from '../views/About'
import Contacts from '../views/Contacts'
import Enter from '../views/Enter'
import Music from '../views/Music'

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: About
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    // component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
    component: About
  },
  {
    path: '/contacts',
    name: 'Contact',
    component: Contacts
  },
  {
    path: '/enter',
    name: 'Enter',
    component: Enter
  },
  {
    path: '/tasks',
    name: 'Music',
    component: Music
  }
];

const router = new VueRouter({
  routes
});

export default router
