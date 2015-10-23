explain 
select key, value
from src s1
group by key, value
having not exists 
 (select distinct s2.key
 from src s2 
 where s2.value = s1.value and 
s2.value > 25);
