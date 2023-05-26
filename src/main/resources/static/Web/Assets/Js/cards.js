const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      cards: [],
      debitCards: [],
      creditCards: [],
      isNewCard: false,
      cardType: "",
      cardColor: "",
      deletedCardNumbers: [],
      isModalOpen: false,
    }
  },
  created() {
    this.getCardsInfo();
  },

  methods: {
    getCardsInfo() {
      axios.get("/api/clients/current")
        .then(response => {
          this.cards = response.data.cards;
          console.log(this.cards);
          this.debitCards = this.cards.filter(card => card.type == "DEBITO" && card.active);
          console.log(this.debitCards);
          this.creditCards = this.cards.filter(card => card.type == "CREDITO" && card.active);
          console.log(this.creditCards);
          console.log(this.cardType);
   


        })
    },
    cardsByType(type) {
      if (this.cards.length < 1) {
        return []
      }
      return this.cards.filter(e => e.cardType == type)
    },
    newCard(type) {
      this.cardType = type
      this.isNewCard = true
    },
    logout() {
      axios.post('/api/logout')
        .then(() => window.location.href = "/web/BigWing/index.html")
    },
    createCard() {
      axios.post('/api/clients/current/cards', "type=" + this.cardType.toUpperCase() + "&color=" + this.cardColor.toUpperCase(), { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
      .then((response) => Swal.fire({
        icon: 'success',
        text: 'Tarjeta creada con éxito',

      }

      ))
        .then(() => window.location.href = "/web/cards.html")
        .catch(error => {
          let errorMessage = error.response.data;
          errorMessage = errorMessage.replace(/\n/g, '<br>'); // Reemplazar saltos de línea por <br> para el formato HTML
      
          Swal.fire({
            icon: 'error',
            title: 'Error',
            html: errorMessage,
            timer: 6000,
            customClass: {
              popup: 'error-popup' // Clase CSS personalizada para el mensaje de error
            }
          });
          
      });
        
    },
    
    eliminarTarjeta(id) {
      Swal.fire({
        title: '¿Estás seguro de que quieres eliminar esta tarjeta?',
        text: "La acción no se podrá revertir",
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí',
        preConfirm: () => {
          return axios.put('/api/clients/current/cards', `id=${id}`)
            .then((response) => Swal.fire({
              icon: 'success',
              text: 'La tarjeta se eliminó correctamente',

            }
            ))
            .then(response => {
              window.location.href = "/web/cards.html"
            })
            .catch(error => {
              Swal.showValidationMessage(
                `Request failed: ${error.response.data}`
              )
            })
        },
      
      })
    },
    isExpired(date) {
      // Lógica para comprobar si la tarjeta está expirada
      const currentDate = new Date();
      const expirationDate = new Date(date);
      return expirationDate < currentDate;
    },
    formatThruDate(thruDate) {
      const date = new Date(thruDate);
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const year = date.getFullYear().toString().slice(2);
      return `${month}/${year}`;
    },
    openModal() {
      this.isModalOpen = true;
    },
    closeModal() {
      this.isModalOpen = false;
      this.cardType = '';
      this.cardColor = '';
    },
    createCardModal() {
      // Lógica para crear la tarjeta desde el modal...
      this.isModalOpen = false;
      this.cardType = '';
      this.cardColor = '';
    },

}
});

app.mount("#app");
