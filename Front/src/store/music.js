export default {
  namespaced: true,
  state: {
    items: [],
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
    }
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
      return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', "http://localhost:8080/rest/transfer", true);

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

        xhr.send({id});

      }).then(response => {
        console.log(typeof(response));
      })
    }
  }
}


