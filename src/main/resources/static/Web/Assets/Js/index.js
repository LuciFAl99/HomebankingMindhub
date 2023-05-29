const { createApp } = Vue

const app = createApp({
    data() {
        return {
            email: "",
            password: "",
            firstName: "",
            lastName: "",
            postEmail: "",
            postPassword: "",
            errorLogin:false,
            confirmPassword:"",
            errorPassCreate:false,
            errorMessage: '',
            errorEmailCreate:false
        }
    },
    methods: {
        login() {

          
            axios.post('/api/login', "email=" + this.email + "&password=" + this.password)
              .then(() => {
                if (this.email === "admin@admin.com") {
                  window.location.href = "/manager.html";
                } else {
                  window.location.href = "/Web/accounts.html";
                }
              })
              .catch(err=>this.errorLogin = true);
              
          },

        register() {
            
            axios.post('/api/clients', "firstName=" + this.firstName + "&lastName=" + this.lastName + "&email=" + this.postEmail + "&password=" + this.postPassword, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                
                .then(() => {

                    axios.post('/api/login', "email=" + this.postEmail + "&password=" + this.postPassword,
                     { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                        .then(() => window.location.href = "/Web/accounts.html")

                })
                .catch(error => {
                  let errorMessage = error.response.data;
                  errorMessage = errorMessage.replace(/\n/g, '<br>'); // Reemplazar saltos de línea por <br> para el formato HTML
              
                  Swal.fire({
                      icon: 'error',
                      title: 'Error',
                      html: errorMessage,
                      timer: 6000,
                  });
              });
        }
        
    },
})

app.mount('#app')