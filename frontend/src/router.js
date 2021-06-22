
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import BookManager from "./components/bookManager"

import SpaceManager from "./components/spaceManager"

import PaymentManager from "./components/paymentManager"


import Mypage from "./components/mypage"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/book',
                name: 'bookManager',
                component: bookManager
            },

            {
                path: '/space',
                name: 'spaceManager',
                component: spaceManager
            },

            {
                path: '/payment',
                name: 'paymentManager',
                component: paymentManager
            },


            {
                path: '/mypage',
                name: 'mypage',
                component: mypage
            },


    ]
})
