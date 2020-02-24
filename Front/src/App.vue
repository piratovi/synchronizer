<template>
  <div id="app" class="page">
    <header class="header">
      <div class="headerWrapper wrapper">
        <div class="headerContent">
          <div class="logoBlock"><img src="assets/vis/logo.svg" alt="" class="logo"></div>
          <nav class="navigation">
            <ul class="navList">
              <li class="item">
                <div class="itemWrapper">
                  <router-link :to="{name: 'About'}">О сайте</router-link>
                </div>
              </li>
              <li class="item">
                <div class="itemWrapper">
                  <router-link :to="{name: 'Enter'}">Войти</router-link>
                </div>
              </li>
              <li class="item">
                <div class="itemWrapper">
                  <router-link :to="{name: 'Music'}">Музыка</router-link>
                </div>
              </li>
              <li class="item">
                <div class="itemWrapper">
                  <router-link :to="{name: 'Contact'}">Контакты</router-link>
                </div>
              </li>
            </ul>
          </nav>
          <div class="userBlock">
            <div class="userInfo">
              <div class="userInfo">Logiiiiiiin19</div>
              <div class="imageBlock"><img src="assets/vis/user.jpg" alt="" class="userImage"></div>
            </div>
          </div>
        </div>
      </div>
    </header>
    <router-view/>
  </div>
<!--  <div id="app">-->
<!--    <div id="nav">-->
<!--      <router-link to="/">Home</router-link> |-->
<!--      <router-link to="/about">About</router-link>-->
<!--    </div>-->
<!--    <router-view/>-->
<!--  </div>-->
</template>

<script>

</script>


<style lang="scss">
  @import "assets/css/style";
</style>
