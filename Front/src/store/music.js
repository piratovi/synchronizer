export default {
  namespaced: true,
  state: {
    items: [],
    ext: []
  },
  getters: {
    all: state => state.items,
  },
  mutations: {
    itemsLoad(state, arr) {
      return state.items = arr;
    },
    console(e) {
      console.log(e);
    },
    extLoad(state,arr) {
      console.log(arr);
      return state.ext = arr;
    },
    /*itemSync(state,arr) {
      return state.items = arr;
    }*/
  },
  actions: {
    itemsLoad({commit}) {
      return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', "http://localhost:8080/rest/syncs", true);

        xhr.onload = function () {
          if (this.status == 200) {

            resolve(this.response);
            //commit('itemsLoad', this.response);

          } else {
            let error = new Error(this.statusText);
            error.code = this.status;
            reject(error);
          }
        };

        xhr.onerror = function () {
          reject(new Error("Network Error"));
        };

        xhr.send();

      }).then(response => {
        let mus = JSON.parse(response);
        commit('itemsLoad', mus);
        return mus;
      })
    },
    itemSync(oper, id) {
      console.log(id);
      return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', "http://localhost:8080/rest/transfertest", true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onload = function () {
          if (this.status == 200) {
            resolve(this.response);
            //commit('itemsLoad', this.response);
          } else {
            let error = new Error(this.statusText);
            error.code = this.status;
            reject(error);
          }
        };

        xhr.onerror = function () {
          reject(new Error("Network Error"));
        };

        xhr.send(JSON.stringify(id));

      }).then(response => {
        console.log(typeof(response));
      })
    },
    extLoad({commit}) {
      return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', "http://localhost:8080/rest/ext", true);

        xhr.onload = function () {
          if (this.status == 200) {
            resolve(this.response);
          } else {
            let error = new Error(this.statusText);
            error.code = this.status;
            reject(error);
          }
        };

        xhr.onerror = function () {
          reject(new Error("Network Error"));
        };

        xhr.send();

      }).then(response => {
        let ext = JSON.parse(response);
        commit('extLoad', ext);
        return ext;
      })
    }
  }
}


