import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
    static AtomicInteger listos = new AtomicInteger(0);
    static AtomicInteger puestos = new AtomicInteger(2);
    static Semaphore s22 = new Semaphore(0,true);

    public static void main(String[] args) {
        Semaphore s1 = new Semaphore(10,true);
        Semaphore s2 = Main.s22;
        ReentrantLock s3 = new ReentrantLock();//semaforo para restringuir un valor abajo se pone tryLock
        //Creacion de los 20 jugadores de forma correcta y se les da un nombre
        for (int i=1;i<=20;i++){
            Jugador j = new Jugador(i,s1,s2,s3);
            j.start();
        }
    }
    public synchronized static void puestoss(Jugador jugador) {//posiciones del 2 al 5 y los demas mueren
        int puesto=Main.puestos.get();

        if (puesto<=5){
            System.out.println("El jugador "+jugador.id+ " ha quedado en  la posicion "+ puesto);
        } else {
            System.out.println("El jugador "+jugador.id+ " no ha llegado a tiempo a la prueba 2 y ha sido descalificado");
        }

        Main.puestos.set(puesto+1);

    }

    public synchronized static void listoss() {//introducir datos en el semaforo 2
        Main.listos.set(Main.listos.get()+1);//sumamos jugadores
        if (Main.listos.get() == 10){//cuando es igual a 10 entrar todos de golpe
            Main.s22.release(10);//mediante el release entra de golpe
        }

    }

}




class Jugador extends Thread {
    int id;
    Semaphore s1;
    Semaphore s2;
    ReentrantLock s3;

    Jugador(int id,Semaphore s1,Semaphore s2,ReentrantLock s3){
        this.id=id;
        this.s1=s1;
        this.s2=s2;
        this.s3=s3;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(((new Random().nextInt(3) ) + 1) * 1000);
            int muerte =  new Random().nextInt(10)+1;
            if (muerte > 9)//10%posibilidades de morir
            {
                System.out.println("El jugador "+this.id+ " ha sido descalificado en la prueba 1");
            }else {
                System.out.println("El jugador "+this.id+ " ha superado la prueba 1");
                if (s1.tryAcquire())// deja pasar a los jugadores que ponemos en el s1 y los que no eliminadoss
                {
                    System.out.println("El jugador "+this.id+ " ha completado a tiempo la prueba 1");
                    Main.listoss();
                    s2.acquire();//cuando main.listoss() tenga 10 entran todos de golpe
                    Thread.sleep(((new Random().nextInt(3) ) + 1) * 1000);
                    int muerte2 =  new Random().nextInt(10)+1;
                    if (muerte2 > 9){
                        System.out.println("El jugador "+this.id+ " ha sido descalificado en la prueba 2");
                    }else {
                        System.out.println("El jugador "+this.id+ " ha superado la prueba 2");
                        if(s3.tryLock()){//deja entrar al ganador y los demas a la funcion main.puestoss
                            System.out.println("El jugador "+this.id+ " ha ganado ");
                        }else{
                            Main.puestoss(this);
                        }
                    }

                } else {
                    System.out.println("El jugador "+this.id+ " no ha completado a tiempo la prueba 1 y ha sido descalificado");
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}

