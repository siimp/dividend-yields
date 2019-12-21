package ee.siimp.dividendyields.stockinfo;

import ee.siimp.dividendyields.common.entity.BaseEntity;
import ee.siimp.dividendyields.stock.Stock;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Väärtpaberite splittimine
 * <p>
 * on aktsiate nimiväärtuse vähendamine aktsiakapitali suuruse muutmiseta,
 * muutub aktsiate arv. Uue nimiväärtusega aktsiatele antakse uus ISIN kood.
 * Aktsiate splittimist võivad kasutada äriühingud,
 * kelle väärtpaberid on suure nimiväärtusega ja seetõttu vähelikviidsed,
 * kuid kelle väärtpaberite vastu oodatakse suurema huvi tekkimist.
 * Splittimine lihtsustab muudatuste tegemist omanike struktuuris ka börsil mittenoteeritud äriühingutel.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockInfo extends BaseEntity {

    @OneToOne(mappedBy = "stockInfo")
    private Stock stock;

    @NotNull
    @Min(0)
    @Column(precision = 21)
    private BigInteger numberOfSecurities;

    @Override
    public String toString() {
        return "StockInfo{" +
                "stock=" + stock.getName() +
                ", numberOfSecurities=" + numberOfSecurities +
                '}';
    }
}
