#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/module.h>
#include <linux/kdev_t.h>
#include <linux/fs.h>
#include <linux/device.h>
#include <linux/delay.h>
#include <linux/gpio.h>    
#include <linux/interrupt.h>
#include <linux/jiffies.h>
#include <asm/io.h>
#include<linux/slab.h>      


extern unsigned long volatile jiffies;
unsigned long old_jiffie = 0;


//LED is connected to this GPIO
#define GPIO_25_IN  (25)

int ret = -1;
char path[] = "/usr/bin/curl";
/*endpoint data and wcId can set here*/
char *argv[] = {path, "-X", "POST", "192.168.0.137:8080/registry/door/2", NULL};
char *envp[] = {NULL};

void workqueue_fn(struct work_struct *work); 
 
/*Creating work by Static Method */
DECLARE_WORK(workqueue,workqueue_fn);
 
/*Workqueue Function*/
void workqueue_fn(struct work_struct *work)
{
  pr_info("Door Opened");
	ret = call_usermodehelper(path, argv, envp, UMH_WAIT_PROC);
  printk("ret=%d\n", ret);
}
 


//This used for storing the IRQ number for the GPIO
unsigned int GPIO_irqNumber;

//Interrupt handler for GPIO 25. This will be called whenever there is a raising edge detected. 
static irqreturn_t gpio_irq_handler(int irq,void *dev_id) 
{
  static unsigned long flags = 0;
  
   unsigned long diff = jiffies - old_jiffie;
   if (diff < 200)
   {
     return IRQ_HANDLED;
   }
  
  old_jiffie = jiffies;

  local_irq_save(flags);

	//tasklet_schedule(&my_tasklet);
  schedule_work(&workqueue);

  local_irq_restore(flags);
  return IRQ_HANDLED;
}
 
static int __init poober_driver_init(void);
static void __exit poober_driver_exit(void);
 
//Module Init function
static int __init poober_driver_init(void)
{ 
  //Input GPIO configuratioin
  //Checking the GPIO is valid or not
  if(gpio_is_valid(GPIO_25_IN) == false){
    pr_err("GPIO %d is not valid\n", GPIO_25_IN);
    goto r_gpio_in;
  }
  
  //Requesting the GPIO
  if(gpio_request(GPIO_25_IN,"GPIO_25_IN") < 0){
    pr_err("ERROR: GPIO %d request\n", GPIO_25_IN);
    goto r_gpio_in;
  }
  
  //configure the GPIO as input
  gpio_direction_input(GPIO_25_IN);
  
  //Debounce the button with a delay of 200ms
  if(gpio_set_debounce(GPIO_25_IN, 100) < 0){
    pr_err("ERROR: gpio_set_debounce - %d\n", GPIO_25_IN);
    //goto r_gpio_in;
  }
  
  //Get the IRQ number for our GPIO
  GPIO_irqNumber = gpio_to_irq(GPIO_25_IN);
  pr_info("GPIO_irqNumber = %d\n", GPIO_irqNumber);
  
  if (request_irq(GPIO_irqNumber,             //IRQ number
                  (void *)gpio_irq_handler,   //IRQ handler
                  IRQF_TRIGGER_HIGH,        //Handler will be called in raising edge
                  "poober_device",               //used to identify the device name using this IRQ
                  NULL)) {                    //device id for shared IRQ
    pr_err("cannot register IRQ ");
    goto r_gpio_in;
  }
  
  pr_info("Device Driver Inserted!\n");
  return 0;

r_gpio_in:
  gpio_free(GPIO_25_IN);

  return -1;
}

// Module exit function
static void __exit poober_driver_exit(void)
{
	//tasklet_kill(&my_tasklet);
  free_irq(GPIO_irqNumber,NULL);
  gpio_free(GPIO_25_IN);
  pr_info("Device Driver Removed!\n");
}
 
module_init(poober_driver_init);
module_exit(poober_driver_exit);
 
MODULE_LICENSE("GPL");
MODULE_AUTHOR("StepSysTerv");
MODULE_DESCRIPTION("KML for signaling door state to Poober backend");
MODULE_VERSION("1.0");
