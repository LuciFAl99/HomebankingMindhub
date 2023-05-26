const { createApp } = Vue

const app = createApp({
  data() {
    return {
      clientes: [],
      firstName: "",
      lastName: "",
      email:"",
      loanName: "",
      maxAmount: 0,
      paymentsText: "",
      interest: ""
    
    }
  },
  created(){
    this.loadData()
  },
  methods: {
    loadData() {
      axios.get("/api/clients")
        .then(response => {
          this.clientes = response.data;
          console.log(this.clientes)
        })
        .catch(error => console.log(error));
        
    },
    logout() {
        axios.post('/api/logout')
            .then(() => window.location.href = "/web/index.html")
    },
    addClient() {
      this.postClient();
    },
    postClient() {
      const newClient = {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email
      };
    
      axios.post('/rest/clients', newClient)
       .then(response => {
         // Agregar el nuevo cliente al array clientes
         this.clientes.push(response.data);
         // Limpiar los campos del formulario
         this.firstName = "";
         this.lastName = "";
         this.email = "";
       })
       .catch(error => console.log(error));
    },
    logout() {
      axios.post('/api/logout')
        .then(() => window.location.href = "/web/BigWing/index.html")
    },
    createLoan() {
      const payments = this.paymentsText.split(",").map(payment => parseInt(payment.trim()));
      
      const loan = {
        name: this.loanName,
        maxAmount: this.maxAmount,
        payments: payments,
        interest: this.interest
      };
      
      axios.post("/api/admin/loan", loan)
      .then((response) => Swal.fire({
        icon: 'success',
        text: 'El préstamo se realizó correctamente',

      }

      ))
      .catch((error) => {
        console.log(error.response.data)
        Swal.fire({
          icon: 'error',
          text: error.response.data,
          confirmButtonColor: "#7c601893",
        })
      })
    }
}
 
})

app.mount('#app')
