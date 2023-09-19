class Registro {
    private long ponteiro;
    private int id;

    // getter's and setter's
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public long getPonteiro()
    {
        return ponteiro;
    }

    public void setPonteiro(long ponteiro)
    {
        this.ponteiro = ponteiro;
    }

    // construtores
    public Registro(long ponteiro, int id) {
        this.ponteiro = ponteiro;
        this.id = id;
    }

    public Registro() {
        ponteiro = 0;
        id = 0;
    }
}